package com.lokavo.ui.article

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.lokavo.R
import com.lokavo.data.Result
import com.lokavo.data.remote.response.ListItem
import com.lokavo.databinding.FragmentArticleBinding
import com.lokavo.ui.adapter.ArticleAdapter
import com.lokavo.utils.showSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArticleViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadArticles()
        return binding.root
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)

        with(binding.rvArticle) {
            this.layoutManager = layoutManager
        }
    }

    private fun observeViewModel() {
        viewModel.articles.observe(viewLifecycleOwner) { res ->
            when (res) {
                is Result.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progress.visibility = View.GONE
                    setArticlesData(res.data.list)
                }

                is Result.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.root.showSnackbar(res.error)
                }

                is Result.Empty -> {
                    binding.progress.visibility = View.GONE
                    binding.root.showSnackbar(getString(R.string.not_found))
                }
            }
        }
    }

    private fun setArticlesData(newsData: List<ListItem?>?) {
        val adapter = ArticleAdapter { url ->
            val intent = Intent(requireContext(), ArticleWebViewActivity::class.java).apply {
                putExtra(ArticleWebViewActivity.EXTRA_URL, url)
            }
            startActivity(intent)
        }
        adapter.submitList(newsData)
        binding.rvArticle.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
