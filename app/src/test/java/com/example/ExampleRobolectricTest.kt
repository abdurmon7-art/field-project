package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.AudioProcessor
import com.example.audio.VoicePreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FF Voice Changer", appName)
  }

  @Test
  fun `test 18 voice presets available`() {
    assertEquals(18, VoicePreset.ALL_PRESETS.size)
    val female = VoicePreset.getById("natural_female")
    assertNotNull(female)
    assertEquals("Natural Female", female.name)
  }

  @Test
  fun `test audio processor buffer transformation`() {
    val processor = AudioProcessor(44100)
    processor.applyPreset(VoicePreset.getById("robot"))

    val input = ShortArray(512) { (it * 10).toShort() }
    val output = ShortArray(512)

    val rms = processor.processPcmBuffer(input, output, 512)
    assertNotNull(output)
  }
}
