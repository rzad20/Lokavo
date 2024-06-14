package com.lokavo.ui.placeDetail

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lokavo.R
import com.lokavo.data.remote.response.DetailsItem
import com.lokavo.data.remote.response.ModelingResultsResponse
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
            binding.placereview.text = getString(R.string.ulasan, it.reviews)
            Glide.with(this).load(it.featuredImage)
                .into(binding.placeImage)
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