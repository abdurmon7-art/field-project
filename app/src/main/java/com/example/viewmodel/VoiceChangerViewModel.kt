package com.example.viewmodel

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioProcessor
import com.example.audio.AudioRecorder
import com.example.audio.RealtimeAudioEngine
import com.example.audio.VoicePreset
import com.example.data.CompatibilityAnalyzer
import com.example.data.CompatibilityReport
import com.example.data.RecordingItem
import com.example.data.RecordingsRepository
import com.example.data.UserSettingsRepository
import com.example.service.VoiceChangerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

enum class PreviewVoiceType {
    ORIGINAL, CHANGED
}

class VoiceChangerViewModel(application: Application) : AndroidViewModel(application) {

    val processor = AudioProcessor()
    val audioEngine = RealtimeAudioEngine(processor)
    val recorder = AudioRecorder(application, processor)
    val recordingsRepo = RecordingsRepository(application)
    val settingsRepo = UserSettingsRepository(application)
    val compatibilityAnalyzer = CompatibilityAnalyzer(application)

    // State flows
    private val _selectedPreset = MutableStateFlow(VoicePreset.getById("natural_female"))
    val selectedPreset: StateFlow<VoicePreset> = _selectedPreset.asStateFlow()

    private val _customPitchFactor = MutableStateFlow(1.0f)
    val customPitchFactor: StateFlow<Float> = _customPitchFactor.asStateFlow()

    private val _customFormantShift = MutableStateFlow(1.0f)
    val customFormantShift: StateFlow<Float> = _customFormantShift.asStateFlow()

    private val _customEffectIntensity = MutableStateFlow(1.0f)
    val customEffectIntensity: StateFlow<Float> = _customEffectIntensity.asStateFlow()

    private val _customReverbMix = MutableStateFlow(0.0f)
    val customReverbMix: StateFlow<Float> = _customReverbMix.asStateFlow()

    private val _customEchoDelayMs = MutableStateFlow(0)
    val customEchoDelayMs: StateFlow<Int> = _customEchoDelayMs.asStateFlow()

    private val _customEchoFeedback = MutableStateFlow(0.0f)
    val customEchoFeedback: StateFlow<Float> = _customEchoFeedback.asStateFlow()

    private val _isNoiseReductionOn = MutableStateFlow(true)
    val isNoiseReductionOn: StateFlow<Boolean> = _isNoiseReductionOn.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _previewVoiceType = MutableStateFlow(PreviewVoiceType.CHANGED)
    val previewVoiceType: StateFlow<PreviewVoiceType> = _previewVoiceType.asStateFlow()

    private val _selectedGamePackage = MutableStateFlow("com.dts.freefireth")
    val selectedGamePackage: StateFlow<String> = _selectedGamePackage.asStateFlow()

    private val _compatibilityReport = MutableStateFlow(compatibilityAnalyzer.analyzeSystem())
    val compatibilityReport: StateFlow<CompatibilityReport> = _compatibilityReport.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private val _currentlyPlayingFilePath = MutableStateFlow<String?>(null)
    val currentlyPlayingFilePath: StateFlow<String?> = _currentlyPlayingFilePath.asStateFlow()

    val isEngineRunning = audioEngine.isEngineRunning
    val micAudioLevel = audioEngine.micAudioLevel
    val isRecording = recorder.isRecording
    val recordingDuration = recorder.recordingDurationSeconds
    val recordingsList = recordingsRepo.recordings
    val userSettings = settingsRepo.settings
    val errorMessage = audioEngine.errorMessage

    init {
        // Load default preset from settings
        val defaultId = userSettings.value.defaultEffectId
        val preset = VoicePreset.getById(defaultId)
        selectPreset(preset)
    }

    fun selectPreset(preset: VoicePreset) {
        _selectedPreset.value = preset
        if (!preset.isCustomizable) {
            _customPitchFactor.value = preset.pitchFactor
            _customFormantShift.value = preset.formantShift
            _customEffectIntensity.value = preset.intensity
            _customReverbMix.value = preset.reverbMix
            _customEchoDelayMs.value = preset.echoDelayMs
            _customEchoFeedback.value = preset.echoFeedback
            _isNoiseReductionOn.value = true
        }
        applyCurrentParametersToProcessor()

        // Update notification if service active
        if (isEngineRunning.value) {
            VoiceChangerService.startService(getApplication(), preset.name)
        }
    }

    fun setPitchFactor(pitch: Float) {
        _customPitchFactor.value = pitch
        applyCurrentParametersToProcessor()
    }

    fun setFormantShift(formant: Float) {
        _customFormantShift.value = formant
        applyCurrentParametersToProcessor()
    }

    fun setEffectIntensity(intensity: Float) {
        _customEffectIntensity.value = intensity
        applyCurrentParametersToProcessor()
    }

    fun setReverbMix(reverb: Float) {
        _customReverbMix.value = reverb
        applyCurrentParametersToProcessor()
    }

    fun setEchoDelayMs(delayMs: Int) {
        _customEchoDelayMs.value = delayMs
        applyCurrentParametersToProcessor()
    }

    fun setEchoFeedback(feedback: Float) {
        _customEchoFeedback.value = feedback
        applyCurrentParametersToProcessor()
    }

    fun toggleNoiseReduction() {
        _isNoiseReductionOn.value = !_isNoiseReductionOn.value
        applyCurrentParametersToProcessor()
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        processor.isMuted = _isMuted.value
    }

    fun setPreviewVoiceType(type: PreviewVoiceType) {
        _previewVoiceType.value = type
        if (type == PreviewVoiceType.ORIGINAL) {
            processor.pitchFactor = 1.0f
            processor.formantShift = 1.0f
            processor.intensity = 0.0f
            processor.reverbMix = 0.0f
            processor.echoDelayMs = 0
            processor.echoFeedback = 0f
        } else {
            applyCurrentParametersToProcessor()
        }
    }

    fun setSelectedGamePackage(packageName: String) {
        _selectedGamePackage.value = packageName
    }

    private fun applyCurrentParametersToProcessor() {
        processor.pitchFactor = _customPitchFactor.value
        processor.formantShift = _customFormantShift.value
        processor.intensity = _customEffectIntensity.value
        processor.reverbMix = _customReverbMix.value
        processor.echoDelayMs = _customEchoDelayMs.value
        processor.echoFeedback = _customEchoFeedback.value
        processor.isNoiseReductionEnabled = _isNoiseReductionOn.value
        processor.filterType = _selectedPreset.value.filterType
        processor.isMuted = _isMuted.value
    }

    fun toggleVoiceChangerEngine() {
        if (isEngineRunning.value) {
            audioEngine.stopEngine()
            VoiceChangerService.stopService(getApplication())
        } else {
            applyCurrentParametersToProcessor()
            audioEngine.startEngine(viewModelScope)
            if (isEngineRunning.value) {
                VoiceChangerService.startService(getApplication(), _selectedPreset.value.name)
            }
        }
    }

    fun startRecording(recordOriginal: Boolean = false) {
        val effectName = if (recordOriginal) "Original Voice" else _selectedPreset.value.name
        recorder.startRecording(
            recordOriginal = recordOriginal,
            effectName = effectName,
            scope = viewModelScope,
            engineAlreadyRunning = isEngineRunning.value
        )
    }

    fun stopRecording() {
        val file = recorder.stopRecording()
        if (file != null) {
            recordingsRepo.refreshRecordings()
        }
    }

    fun playRecording(item: RecordingItem) {
        stopPlayback()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(item.filePath)
                prepare()
                start()
                setOnCompletionListener {
                    stopPlayback()
                }
            }
            _currentlyPlayingFilePath.value = item.filePath
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlayback()
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        _currentlyPlayingFilePath.value = null
    }

    fun deleteRecording(item: RecordingItem) {
        if (_currentlyPlayingFilePath.value == item.filePath) {
            stopPlayback()
        }
        recordingsRepo.deleteRecording(item)
    }

    fun shareRecording(item: RecordingItem) {
        recordingsRepo.shareRecording(item)
    }

    fun refreshSystemCompatibility() {
        _compatibilityReport.value = compatibilityAnalyzer.analyzeSystem()
    }

    fun clearError() {
        audioEngine.clearError()
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stopEngine()
        stopPlayback()
    }
}
