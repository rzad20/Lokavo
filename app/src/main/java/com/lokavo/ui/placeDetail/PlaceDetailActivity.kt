package com.lokavo.ui.placeDetail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lokavo.R
import com.lokavo.data.remote.response.DetailsItem
import com.lokavo.databinding.ActivityPlaceDetailBinding
import com.lokavo.ui.detailAnalysis.DetailAnalysisActivity

class PlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val result = intent.getParcelableExtra(DetailAnalysisActivity.RESULT) ?: DetailsItem()

        result.let {
            binding.textPlaceName.text = it.name
            binding.textPlaceLocation.text = it.address
            binding.placeRating.text = it.rating.toString()
            binding.placeCategory.text = it.mainCategory
            binding.placereview.text = getString(R.string.ulasan, it.reviews)
            Glide.with(this).load(it.featuredImage)
                .into(binding.placeImage)
            val popularTimes =
                it.mostPopularTimes?.joinToString(", ") { timeItem -> timeItem?.timeLabel ?: "" }
            val visitTimesText = if (!popularTimes.isNullOrEmpty()) {
                getString(R.string.visit_times, popularTimes)
            } else {
                getString(R.string.no_popular_times)
            }
            binding.txtPopular.text = visitTimesText

            it.reviewsPerRating?.let { reviewsPerRating ->
                binding.rating5.text =
                    getString(R.string.rating_count, 5, reviewsPerRating.five ?: 0)
                binding.rating4.text =
                    getString(R.string.rating_count, 4, reviewsPerRating.four ?: 0)
                binding.rating3.text =
                    getString(R.string.rating_count, 3, reviewsPerRating.three ?: 0)
                binding.rating2.text =
                    getString(R.string.rating_count, 2, reviewsPerRating.two ?: 0)
                binding.rating1.text =
                    getString(R.string.rating_count, 1, reviewsPerRating.one ?: 0)
            }
        }
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

    companion object {
        const val RESULT = "result"
    }
}