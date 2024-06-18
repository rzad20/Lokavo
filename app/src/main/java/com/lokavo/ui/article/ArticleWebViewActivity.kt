package com.lokavo.ui.article

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.lokavo.databinding.ActivityArticleWebViewBinding
import com.google.android.material.progressindicator.LinearProgressIndicator

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
        if (url != null) {
            binding.topAppBar.title = url
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progress.visibility = LinearProgressIndicator.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progress.visibility = LinearProgressIndicator.GONE
            }
        }
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
