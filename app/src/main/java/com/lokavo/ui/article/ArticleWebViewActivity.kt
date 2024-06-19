package com.lokavo.ui.article

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.lokavo.databinding.ActivityArticleWebViewBinding

class ArticleWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleWebViewBinding
    private var allowedUrl: String? = null

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

        allowedUrl = intent.getStringExtra(EXTRA_URL)
        if (allowedUrl != null) {
            binding.topAppBar.title = allowedUrl
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: android.graphics.Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                binding.progress.visibility = LinearProgressIndicator.VISIBLE
                if (url != allowedUrl) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progress.visibility = LinearProgressIndicator.GONE
            }
        }
        binding.webView.settings.javaScriptEnabled = true
        allowedUrl?.let {
            binding.webView.loadUrl(it)
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
