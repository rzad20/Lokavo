package com.lokavo.ui.article

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.lokavo.databinding.ActivityArticleWebViewBinding

class ArticleWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val url = intent.getStringExtra(EXTRA_URL)
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        if (url != null) {
            binding.webView.loadUrl(url)
        }
    }

    companion object {
        const val EXTRA_URL = "extra_url"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
