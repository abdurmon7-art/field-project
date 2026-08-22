package com.example.data

data class RecordingItem(
    val id: String,
    val title: String,
    val filePath: String,
    val durationSeconds: Int,
    val sizeBytes: Long,
    val timestampMs: Long,
    val effectName: String,
    val isOriginal: Boolean = false
) {
    val formattedDuration: String
        get() {
            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            return String.format("%02d:%02d", mins, secs)
        }

    val formattedSize: String
        get() {
            val kb = sizeBytes / 1024.0
            return if (kb < 1024) {
                String.format("%.1f KB", kb)
            } else {
                String.format("%.1f MB", kb / 1024.0)
            }
        }
}
