package com.example.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
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
import java.util.concurrent.atomic.AtomicBoolean

enum class LatencyMode {
    ULTRA_LOW, BALANCED, HIGH_COMPATIBILITY
}

class RealtimeAudioEngine(
    val processor: AudioProcessor = AudioProcessor()
) {
    private val TAG = "RealtimeAudioEngine"

    private val _isEngineRunning = MutableStateFlow(false)
    val isEngineRunning: StateFlow<Boolean> = _isEngineRunning.asStateFlow()

    private val _micAudioLevel = MutableStateFlow(0f) // 0.0 to 1.0 RMS
    val micAudioLevel: StateFlow<Float> = _micAudioLevel.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var latencyMode: LatencyMode = LatencyMode.BALANCED

    private var processingJob: Job? = null
    private val isRunning = AtomicBoolean(false)

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null

    // Dual mode: record transformed audio to file while running
    var activeRecorder: AudioRecorder? = null

    @SuppressLint("MissingPermission")
    fun startEngine(scope: CoroutineScope) {
        if (isRunning.get()) return

        _errorMessage.value = null

        val sampleRate = 44100
        processor.sampleRate = sampleRate

        val channelConfigIn = AudioFormat.CHANNEL_IN_MONO
        val channelConfigOut = AudioFormat.CHANNEL_OUT_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT

        val minBufIn = AudioRecord.getMinBufferSize(sampleRate, channelConfigIn, audioFormat)
        val minBufOut = AudioTrack.getMinBufferSize(sampleRate, channelConfigOut, audioFormat)

        if (minBufIn <= 0 || minBufOut <= 0) {
            _errorMessage.value = "Audio device hardware buffer error."
            return
        }

        val bufferMultiplier = when (latencyMode) {
            LatencyMode.ULTRA_LOW -> 1
            LatencyMode.BALANCED -> 2
            LatencyMode.HIGH_COMPATIBILITY -> 4
        }

        val inBufferSize = minBufIn * bufferMultiplier
        val outBufferSize = minBufOut * bufferMultiplier

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfigIn,
                audioFormat,
                inBufferSize
            )

            audioTrack = AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelConfigOut)
                        .build()
                )
                .setBufferSizeInBytes(outBufferSize)
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .build()

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED ||
                audioTrack?.state != AudioTrack.STATE_INITIALIZED
            ) {
                _errorMessage.value = "Failed to initialize microphone or speaker track."
                releaseAudioResources()
                return
            }

            audioRecord?.startRecording()
            audioTrack?.play()

            isRunning.set(true)
            _isEngineRunning.value = true

            processingJob = scope.launch(Dispatchers.IO) {
                val chunkSize = 512 * bufferMultiplier
                val inputBuffer = ShortArray(chunkSize)
                val outputBuffer = ShortArray(chunkSize)

                while (isActive && isRunning.get()) {
                    val readCount = audioRecord?.read(inputBuffer, 0, chunkSize) ?: 0
                    if (readCount > 0) {
                        val rms = processor.processPcmBuffer(inputBuffer, outputBuffer, readCount)
                        _micAudioLevel.value = (rms * 4.0f).coerceIn(0.0f, 1.0f)

                        // Play processed audio
                        audioTrack?.write(outputBuffer, 0, readCount)

                        // If recorder is active, record processed or original frame
                        activeRecorder?.onAudioChunkProcessed(outputBuffer, readCount)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Audio engine start failed", e)
            _errorMessage.value = "Microphone error: ${e.localizedMessage ?: "Unknown"}"
            stopEngine()
        }
    }

    fun stopEngine() {
        if (!isRunning.getAndSet(false)) return
        _isEngineRunning.value = false

        processingJob?.cancel()
        processingJob = null

        releaseAudioResources()
        _micAudioLevel.value = 0f
    }

    private fun releaseAudioResources() {
        try {
            audioRecord?.let {
                if (it.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioRecord", e)
        }
        audioRecord = null

        try {
            audioTrack?.let {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioTrack", e)
        }
        audioTrack = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
