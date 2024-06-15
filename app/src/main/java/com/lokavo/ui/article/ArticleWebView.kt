package com.lokavo.ui.article

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.lokavo.databinding.FragmentArticleWebViewBinding

class ArticleWebView : Fragment() {
    private var _binding: FragmentArticleWebViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString(EXTRA_URL)
        if (url != null) {
            binding.webView.webViewClient = WebViewClient()
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.loadUrl(url)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_URL = "extra_url"
        fun newInstance(url: String?): ArticleWebView {
            val fragment = ArticleWebView()
            val args = Bundle()
            args.putString(EXTRA_URL, url)
            fragment.arguments = args
            return fragment
        }
    }
}
