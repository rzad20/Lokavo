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
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (requireContext().isOnline()) {
            observeViewModel()
            viewModel.loadArticles()
        } else {
            binding.root.showSnackbarOnNoConnection(requireContext())
            binding.btnTryAgain.visibility = View.VISIBLE
            binding.error.visibility = View.VISIBLE
            binding.error.text = getString(R.string.no_internet_connection)
        }

        binding.btnTryAgain.setOnClickListener {
            if (requireContext().isOnline()) {
                observeViewModel()
                viewModel.loadArticles()
            } else {
                binding.root.showSnackbarOnNoConnection(requireContext())
                binding.btnTryAgain.visibility = View.VISIBLE
                binding.error.visibility = View.VISIBLE
                binding.error.text = getString(R.string.no_internet_connection)
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)

        with(binding.rvArticle) {
            this.layoutManager = layoutManager
        }
    }

    private fun observeViewModel() {
        setupRecyclerView()
        viewModel.articles.observe(viewLifecycleOwner) { res ->
            when (res) {
                is Result.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.error.visibility = View.GONE
                    binding.btnTryAgain.visibility = View.GONE
                }

                is Result.Success -> {
                    binding.progress.visibility = View.GONE
                    setArticlesData(res.data.list)
                    binding.error.visibility = View.GONE
                    binding.btnTryAgain.visibility = View.GONE
                    viewModel.hasLoaded = true
                }

                is Result.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.root.showSnackbar(res.error)
                    binding.error.text = res.error
                    binding.error.visibility = View.VISIBLE
                    binding.btnTryAgain.visibility = View.VISIBLE
                }

                is Result.Empty -> {
                    binding.progress.visibility = View.GONE
                    binding.root.showSnackbar(getString(R.string.not_found))
                    binding.error.text = getString(R.string.not_found)
                    binding.error.visibility = View.VISIBLE
                    binding.btnTryAgain.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        if (viewModel.hasLoaded) {
            binding.btnTryAgain.visibility = View.GONE
            binding.error.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
