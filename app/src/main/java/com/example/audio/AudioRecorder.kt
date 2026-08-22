package com.example.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile

class AudioRecorder(
    private val context: Context,
    private val processor: AudioProcessor
) {
    private val TAG = "AudioRecorder"

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    private var currentFile: File? = null
    private var fileOutputStream: FileOutputStream? = null
    private var totalBytesWritten = 0L

    private var timerJob: Job? = null
    private var standaloneRecordingJob: Job? = null
    private var recordOriginalOnly = false

    fun startRecording(
        recordOriginal: Boolean = false,
        effectName: String = "Changed Voice",
        scope: CoroutineScope,
        engineAlreadyRunning: Boolean = false
    ): File? {
        if (_isRecording.value) return currentFile

        this.recordOriginalOnly = recordOriginal

        val dir = File(context.filesDir, "voice_recordings")
        if (!dir.exists()) dir.mkdirs()

        val fileName = "FF_Voice_${System.currentTimeMillis()}.wav"
        val outputFile = File(dir, fileName)
        currentFile = outputFile

        try {
            fileOutputStream = FileOutputStream(outputFile)
            writeWavHeader(fileOutputStream!!, 44100, 1, 16, 0)
            totalBytesWritten = 0

            _isRecording.value = true
            _recordingDurationSeconds.value = 0

            timerJob = scope.launch(Dispatchers.Default) {
                var seconds = 0
                while (isActive && _isRecording.value) {
                    kotlinx.coroutines.delay(1000)
                    seconds++
                    _recordingDurationSeconds.value = seconds
                }
            }

            // If engine is not running real-time, record standalone in background thread
            if (!engineAlreadyRunning) {
                startStandaloneMicRecording(scope, 44100)
            }

            return outputFile
        } catch (e: Exception) {
            Log.e(TAG, "Error starting recorder", e)
            return null
        }
    }

    private fun startStandaloneMicRecording(scope: CoroutineScope, sampleRate: Int) {
        standaloneRecordingJob = scope.launch(Dispatchers.IO) {
            val minBuf = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val buffer = ShortArray(minBuf)
            val processedBuffer = ShortArray(minBuf)

            @Suppress("MissingPermission")
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBuf * 2
            )

            try {
                recorder.startRecording()
                while (isActive && _isRecording.value) {
                    val read = recorder.read(buffer, 0, minBuf)
                    if (read > 0) {
                        if (recordOriginalOnly) {
                            onAudioChunkProcessed(buffer, read)
                        } else {
                            processor.processPcmBuffer(buffer, processedBuffer, read)
                            onAudioChunkProcessed(processedBuffer, read)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Standalone recording error", e)
            } finally {
                try {
                    recorder.stop()
                    recorder.release()
                } catch (e: Exception) {
                    Log.e(TAG, "Error stopping recorder", e)
                }
            }
        }
    }

    @Synchronized
    fun onAudioChunkProcessed(buffer: ShortArray, length: Int) {
        if (!_isRecording.value || fileOutputStream == null) return
        try {
            val byteBuffer = ByteArray(length * 2)
            for (i in 0 until length) {
                val s = buffer[i].toInt()
                byteBuffer[i * 2] = (s and 0x00FF).toByte()
                byteBuffer[i * 2 + 1] = ((s shr 8) and 0x00FF).toByte()
            }
            fileOutputStream?.write(byteBuffer)
            totalBytesWritten += byteBuffer.size
        } catch (e: Exception) {
            Log.e(TAG, "Error writing pcm chunk", e)
        }
    }

    @Synchronized
    fun stopRecording(): File? {
        if (!_isRecording.value) return currentFile

        _isRecording.value = false
        timerJob?.cancel()
        timerJob = null

        standaloneRecordingJob?.cancel()
        standaloneRecordingJob = null

        try {
            fileOutputStream?.flush()
            fileOutputStream?.close()
            fileOutputStream = null

            // Update WAV header with true file size
            currentFile?.let { file ->
                updateWavHeaderSizes(file, totalBytesWritten)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error closing wav recording file", e)
        }

        val result = currentFile
        return result
    }

    private fun writeWavHeader(
        out: FileOutputStream,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int,
        pcmDataSize: Int
    ) {
        val totalDataLen = pcmDataSize + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // Subchunk1Size (16 for PCM)
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // AudioFormat (1 for PCM)
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = blockAlign.toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (pcmDataSize and 0xff).toByte()
        header[41] = (pcmDataSize shr 8 and 0xff).toByte()
        header[42] = (pcmDataSize shr 16 and 0xff).toByte()
        header[43] = (pcmDataSize shr 24 and 0xff).toByte()

        out.write(header, 0, 44)
    }

    private fun updateWavHeaderSizes(file: File, pcmDataSize: Long) {
        try {
            RandomAccessFile(file, "rw").use { raf ->
                val totalDataLen = pcmDataSize + 36
                raf.seek(4)
                raf.write((totalDataLen and 0xff).toInt())
                raf.write((totalDataLen shr 8 and 0xff).toInt())
                raf.write((totalDataLen shr 16 and 0xff).toInt())
                raf.write((totalDataLen shr 24 and 0xff).toInt())

                raf.seek(40)
                raf.write((pcmDataSize and 0xff).toInt())
                raf.write((pcmDataSize shr 8 and 0xff).toInt())
                raf.write((pcmDataSize shr 16 and 0xff).toInt())
                raf.write((pcmDataSize shr 24 and 0xff).toInt())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating wav header size", e)
        }
    }
}
