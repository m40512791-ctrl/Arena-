package com.example.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

class AudioTtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    var isInitialized by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var currentChapterIndex by mutableIntStateOf(1)
        private set

    var playbackSpeed by mutableFloatStateOf(1.0f)
        private set

    var statusMessage by mutableStateOf("Ready to narrate")
        private set

    init {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isPlaying = true
                statusMessage = "Narrating Chapter $currentChapterIndex..."
            }

            override fun onDone(utteranceId: String?) {
                isPlaying = false
                statusMessage = "Chapter narration finished"
            }

            override fun onError(utteranceId: String?) {
                isPlaying = false
                statusMessage = "Audio narration interrupted"
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isInitialized = true
        } else {
            statusMessage = "TTS initialization failed"
        }
    }

    fun playChapter(chapterIndex: Int, title: String, text: String) {
        if (!isInitialized) return
        stop()
        currentChapterIndex = chapterIndex
        tts?.setSpeechRate(playbackSpeed)
        val speechText = "$title. $text"
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "chapter_$chapterIndex"
        tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "chapter_$chapterIndex")
        isPlaying = true
    }

    fun pauseOrStop() {
        if (isPlaying) {
            tts?.stop()
            isPlaying = false
            statusMessage = "Playback paused"
        }
    }

    fun stop() {
        tts?.stop()
        isPlaying = false
        statusMessage = "Stopped"
    }

    fun setSpeed(speed: Float) {
        playbackSpeed = speed
        tts?.setSpeechRate(speed)
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
