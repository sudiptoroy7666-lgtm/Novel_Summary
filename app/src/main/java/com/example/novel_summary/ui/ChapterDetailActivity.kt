package com.example.novel_summary.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.novel_summary.R
import com.example.novel_summary.databinding.ActivitySummaryBinding
import com.example.novel_summary.utils.ToastUtils
import java.util.*

class ChapterDetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivitySummaryBinding

    private var textToSpeech: TextToSpeech? = null
    private var isSpeaking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupTextToSpeech()
        setupUI()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Hide buttons not needed for viewing saved chapters
        binding.btnTextToSpeech.isVisible = true
        binding.btnSaveSummary.isVisible = false
        binding.btnSummaryMenu.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                ToastUtils.showError(this, "TTS language not supported")
            }
        } else {
            ToastUtils.showError(this, "TTS initialization failed")
        }
    }

    private fun setupUI() {
        val chapterName = intent.getStringExtra("CHAPTER_NAME") ?: "Untitled Chapter"
        val summaryText = intent.getStringExtra("SUMMARY_TEXT") ?: ""
        val summaryType = intent.getStringExtra("SUMMARY_TYPE") ?: "summary"

        binding.tvSummaryTitle.text = chapterName
        binding.tvSummaryContent.text = summaryText

        binding.btnTextToSpeech.setOnClickListener {
            if (isSpeaking) {
                textToSpeech?.stop()
                isSpeaking = false
                binding.btnTextToSpeech.text = "Text to Speech"
            } else {
                speakText(summaryText)
                isSpeaking = true
                binding.btnTextToSpeech.text = "Stop"
            }
        }
    }

    private fun speakText(text: String) {
        textToSpeech?.setOnUtteranceProgressListener(object :
            android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Speaking started
            }

            override fun onDone(utteranceId: String?) {
                runOnUiThread {
                    isSpeaking = false
                    binding.btnTextToSpeech.text = "Text to Speech"
                }
            }

            override fun onError(utteranceId: String?) {
                runOnUiThread {
                    isSpeaking = false
                    binding.btnTextToSpeech.text = "Text to Speech"
                }
            }
        })

        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, this.hashCode().toString())
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}