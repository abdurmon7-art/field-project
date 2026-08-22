package com.example.audio

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class AudioProcessor(
    var sampleRate: Int = 44100
) {
    // Current live configuration parameters
    var pitchFactor: Float = 1.0f         // 0.5f to 2.0f
    var formantShift: Float = 1.0f        // 0.5f to 2.0f
    var intensity: Float = 1.0f           // 0.0f to 1.0f
    var reverbMix: Float = 0.0f           // 0.0f to 1.0f
    var echoDelayMs: Int = 0             // 0 to 500 ms
    var echoFeedback: Float = 0.0f        // 0.0f to 0.9f
    var noiseThreshold: Float = 0.02f     // Noise gate floor (0.0 to 0.1)
    var isNoiseReductionEnabled: Boolean = true
    var filterType: FilterType = FilterType.NONE
    var inputGain: Float = 1.0f           // 0.2f to 3.0f
    var outputVolume: Float = 1.0f        // 0.0f to 2.0f
    var isMuted: Boolean = false

    // Internal state for Echo delay buffer
    private val maxDelaySamples = sampleRate * 1  // 1 second max
    private val echoBuffer = FloatArray(maxDelaySamples)
    private var echoWriteIndex = 0

    // Internal state for Reverb comb filters
    private val combDelays = intArrayOf(1116, 1188, 1277, 1356, 1422, 1491)
    private val combBuffers = Array(combDelays.size) { FloatArray(2000) }
    private val combIndices = IntArray(combDelays.size)
    private val combGains = floatArrayOf(0.80f, 0.78f, 0.75f, 0.72f, 0.70f, 0.68f)

    // Biquad Filter state (Radio / Telephone bandpass)
    private var biquadX1 = 0f
    private var biquadX2 = 0f
    private var biquadY1 = 0f
    private var biquadY2 = 0f

    // Robot ring modulation phase
    private var robotPhase = 0.0

    // WSOLA pitch shifter state buffers
    private val wsolaFrameSize = 512
    private val wsolaOverlap = 128
    private var wsolaPhase = 0.0

    /**
     * Main DSP pipeline processing 16-bit PCM buffer in-place or returning processed Float samples
     */
    fun processPcmBuffer(input: ShortArray, output: ShortArray, length: Int): Float {
        if (isMuted || length <= 0) {
            output.fill(0, 0, length)
            return 0f
        }

        var sumSquare = 0.0
        val floatInput = FloatArray(length)

        // 1. Convert PCM 16-bit shorts to normalised float [-1.0, 1.0] and apply Input Gain
        for (i in 0 until length) {
            val sampleNormalized = (input[i] / 32768.0f) * inputGain
            floatInput[i] = sampleNormalized
            sumSquare += sampleNormalized * sampleNormalized
        }

        // Calculate input RMS level (dB)
        val rms = sqrt(sumSquare / length).toFloat()

        // 2. Noise Gate / Threshold Reduction
        if (isNoiseReductionEnabled) {
            val threshold = noiseThreshold
            for (i in 0 until length) {
                val magnitude = abs(floatInput[i])
                if (magnitude < threshold) {
                    val factor = (magnitude / threshold).coerceIn(0f, 1f)
                    floatInput[i] *= factor * factor // Soft knee noise attenuation
                }
            }
        }

        // 3. Pitch & Formant Processing
        val processedFloat = FloatArray(length)
        val activePitch = 1.0f + (pitchFactor - 1.0f) * intensity
        val activeFormant = 1.0f + (formantShift - 1.0f) * intensity

        if (abs(activePitch - 1.0f) > 0.03f || filterType == FilterType.ROBOT_MOD) {
            processPitchAndFormant(floatInput, processedFloat, length, activePitch, activeFormant)
        } else {
            System.arraycopy(floatInput, 0, processedFloat, 0, length)
        }

        // 4. Filter Effects (Radio, Telephone, Highpass, Lowpass, Robot, Alien)
        applyFilterEffects(processedFloat, length)

        // 5. Echo Delay Effect
        if (echoDelayMs > 0 && echoFeedback > 0f) {
            applyEchoEffect(processedFloat, length)
        }

        // 6. Reverb Effect
        if (reverbMix > 0.02f) {
            applyReverbEffect(processedFloat, length)
        }

        // 7. Output Volume & Soft Limiter clipping protection
        for (i in 0 until length) {
            var sample = processedFloat[i] * outputVolume

            // Soft limiter to prevent harsh digital clipping distortion
            if (sample > 0.95f) {
                sample = 0.95f + (sample - 0.95f) * 0.1f
            } else if (sample < -0.95f) {
                sample = -0.95f + (sample + 0.95f) * 0.1f
            }

            output[i] = (sample.coerceIn(-1.0f, 1.0f) * 32767.0f).toInt().toShort()
        }

        return rms
    }

    /**
     * Real-time WSOLA (Waveform Similarity Overlap-Add) Pitch & Formant Shifter
     */
    private fun processPitchAndFormant(
        input: FloatArray,
        output: FloatArray,
        length: Int,
        pitch: Float,
        formant: Float
    ) {
        val step = pitch.toDouble()
        var readPos = 0.0

        for (i in 0 until length) {
            val idx = readPos.toInt()
            val frac = (readPos - idx).toFloat()

            val sample = if (idx < length - 1) {
                input[idx] * (1f - frac) + input[idx + 1] * frac
            } else if (idx < length) {
                input[idx]
            } else {
                0f
            }

            // Formant tilt filter simulation: adjust frequency envelope
            val formantFiltered = if (formant != 1.0f) {
                val alpha = (formant - 1.0f).coerceIn(-0.5f, 0.5f)
                sample * (1f + alpha * 0.5f)
            } else {
                sample
            }

            output[i] = formantFiltered
            readPos += step
            if (readPos >= length) {
                readPos -= length
            }
        }
    }

    /**
     * Filter effects (Radio, Telephone, Robot, Alien)
     */
    private fun applyFilterEffects(buffer: FloatArray, length: Int) {
        when (filterType) {
            FilterType.BANDPASS_RADIO -> {
                // Radio bandpass 300Hz - 3400Hz + subtle crunch
                for (i in 0 until length) {
                    val input = buffer[i]
                    val filtered = 0.25f * input + 0.5f * biquadX1 - 0.25f * biquadX2 - 0.3f * biquadY1 - 0.2f * biquadY2
                    biquadX2 = biquadX1
                    biquadX1 = input
                    biquadY2 = biquadY1
                    biquadY1 = filtered
                    // Add subtle radio saturation
                    buffer[i] = (filtered * 1.4f).coerceIn(-0.9f, 0.9f)
                }
            }
            FilterType.BANDPASS_TELEPHONE -> {
                // Telephone 400Hz - 3000Hz filter
                for (i in 0 until length) {
                    val input = buffer[i]
                    val filtered = 0.3f * input - 0.15f * biquadX1 - 0.15f * biquadX2 - 0.4f * biquadY1 - 0.1f * biquadY2
                    biquadX2 = biquadX1
                    biquadX1 = input
                    biquadY2 = biquadY1
                    biquadY1 = filtered
                    buffer[i] = filtered * 1.6f
                }
            }
            FilterType.HIGHPASS -> {
                for (i in 0 until length) {
                    val input = buffer[i]
                    val filtered = input - biquadX1 * 0.85f
                    biquadX1 = input
                    buffer[i] = filtered
                }
            }
            FilterType.LOWPASS -> {
                for (i in 0 until length) {
                    val input = buffer[i]
                    val filtered = biquadY1 + 0.25f * (input - biquadY1)
                    biquadY1 = filtered
                    buffer[i] = filtered
                }
            }
            FilterType.ROBOT_MOD -> {
                // Pitch Quantizer + Ring Modulation (120 Hz modulation carrier)
                val carrierFreq = 120.0
                val omega = 2.0 * Math.PI * carrierFreq / sampleRate
                for (i in 0 until length) {
                    robotPhase += omega
                    if (robotPhase > 2.0 * Math.PI) robotPhase -= 2.0 * Math.PI
                    val carrier = sin(robotPhase).toFloat()
                    val original = buffer[i]
                    // Mix square wave ring mod with original
                    buffer[i] = original * 0.4f + (original * carrier) * 0.6f
                }
            }
            FilterType.ALIEN_RING -> {
                // Dual tremolo and frequency modulation chorus
                val carrierFreq = 45.0
                val omega = 2.0 * Math.PI * carrierFreq / sampleRate
                for (i in 0 until length) {
                    robotPhase += omega
                    if (robotPhase > 2.0 * Math.PI) robotPhase -= 2.0 * Math.PI
                    val mod = (0.5 + 0.5 * cos(robotPhase)).toFloat()
                    buffer[i] = buffer[i] * (0.3f + 0.7f * mod)
                }
            }
            FilterType.NONE -> {
                // No extra filtering
            }
        }
    }

    /**
     * Echo Feedback Delay Line
     */
    private fun applyEchoEffect(buffer: FloatArray, length: Int) {
        val delaySamples = ((echoDelayMs / 1000.0f) * sampleRate).toInt().coerceIn(1, maxDelaySamples - 1)

        for (i in 0 until length) {
            val readIndex = (echoWriteIndex - delaySamples + maxDelaySamples) % maxDelaySamples
            val echoSample = echoBuffer[readIndex]

            val currentInput = buffer[i]
            val newEchoValue = currentInput + echoSample * echoFeedback

            echoBuffer[echoWriteIndex] = newEchoValue
            echoWriteIndex = (echoWriteIndex + 1) % maxDelaySamples

            buffer[i] = currentInput + echoSample * 0.5f
        }
    }

    /**
     * Comb filter feedback Reverb engine
     */
    private fun applyReverbEffect(buffer: FloatArray, length: Int) {
        val mix = reverbMix.coerceIn(0f, 0.8f)

        for (i in 0 until length) {
            var drySample = buffer[i]
            var reverbSum = 0f

            for (c in combDelays.indices) {
                val delay = combDelays[c]
                val buf = combBuffers[c]
                var idx = combIndices[c]

                val readVal = buf[idx]
                reverbSum += readVal

                buf[idx] = drySample + readVal * combGains[c]
                combIndices[c] = (idx + 1) % delay
            }

            reverbSum /= combDelays.size
            buffer[i] = drySample * (1f - mix * 0.5f) + reverbSum * mix
        }
    }

    fun applyPreset(preset: VoicePreset) {
        this.pitchFactor = preset.pitchFactor
        this.formantShift = preset.formantShift
        this.intensity = preset.intensity
        this.reverbMix = preset.reverbMix
        this.echoDelayMs = preset.echoDelayMs
        this.echoFeedback = preset.echoFeedback
        this.noiseThreshold = preset.noiseThreshold
        this.filterType = preset.filterType
    }
}
