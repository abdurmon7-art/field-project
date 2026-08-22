package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class RecordingsRepository(
    private val context: Context
) {
    private val _recordings = MutableStateFlow<List<RecordingItem>>(emptyList())
    val recordings: StateFlow<List<RecordingItem>> = _recordings.asStateFlow()

    init {
        refreshRecordings()
    }

    fun refreshRecordings() {
        val dir = File(context.filesDir, "voice_recordings")
        if (!dir.exists()) {
            dir.mkdirs()
            _recordings.value = emptyList()
            return
        }

        val files = dir.listFiles { file -> file.extension == "wav" } ?: emptyArray()
        val list = files.map { file ->
            val size = file.length()
            val sampleRate = 44100
            val bytesPerSec = sampleRate * 2 // 16-bit mono
            val pcmBytes = if (size > 44) size - 44 else 0
            val durationSecs = (pcmBytes / bytesPerSec).toInt().coerceAtLeast(1)

            val nameNoExt = file.nameWithoutExtension
            val parts = nameNoExt.split("_")
            val isOriginal = nameNoExt.contains("Original", ignoreCase = true)
            val effectName = if (isOriginal) "Original Voice" else "Changed Voice"

            RecordingItem(
                id = file.name,
                title = nameNoExt.replace("_", " "),
                filePath = file.absolutePath,
                durationSeconds = durationSecs,
                sizeBytes = size,
                timestampMs = file.lastModified(),
                effectName = effectName,
                isOriginal = isOriginal
            )
        }.sortedByDescending { it.timestampMs }

        _recordings.value = list
    }

    fun deleteRecording(item: RecordingItem): Boolean {
        val file = File(item.filePath)
        if (file.exists()) {
            val deleted = file.delete()
            refreshRecordings()
            return deleted
        }
        return false
    }

    fun shareRecording(item: RecordingItem) {
        val file = File(item.filePath)
        if (!file.exists()) return

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/wav"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share Voice Recording")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun renameRecording(item: RecordingItem, newTitle: String): Boolean {
        val file = File(item.filePath)
        if (!file.exists()) return false

        val sanitized = newTitle.replace("[^a-zA-Z0-9_ -]".toRegex(), "")
        val newFile = File(file.parentFile, "$sanitized.wav")
        val renamed = file.renameTo(newFile)
        if (renamed) {
            refreshRecordings()
        }
        return renamed
    }
}
