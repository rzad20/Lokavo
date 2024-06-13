package com.lokavo.ui.detailAnalysis

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import com.lokavo.data.Result
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.R
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.databinding.ActivityDetailAnalysisBinding
import com.lokavo.ui.chatbot.ChatBotActivity
import com.lokavo.ui.forgotPassword.ForgotPasswordActivity
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailAnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailAnalysisBinding
    private lateinit var result: ModelingResultsResponse
    private val viewModel: DetailAnalysisViewModel by viewModel()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val animatedImageButton: ImageButton = binding.animatedImageButton
        animatedImageButton.post {
            val drawable = animatedImageButton.drawable as? AnimatedVectorDrawable
            drawable?.start()
        }

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        result = intent.getParcelableExtra(RESULT) ?: ModelingResultsResponse()

        binding.txtSentimentCategory.text = result.summaryHeader
        binding.detailAnalysis.text = "${result.longInterpretation}"

        if (result.summaryHeader.equals("highly competitive", true)) {
            binding.iconCompetitive.setImageResource(R.drawable.iconhighly)
        } else if (result.summaryHeader.equals("fairly competitive", true)) {
            binding.iconCompetitive.setImageResource(R.drawable.iconfairly)
        }

        val anyChartView = binding.anyChartView
        setupPieChart(anyChartView)

        binding.animatedImageButton.setOnClickListener {
            result.latLng.let {
                if (it != null) {
                    if (uid != null) {
                        postChatBot(it.latitude, it.longitude, uid)
                    }
                }
            }
        }
    }

    private fun postChatBot(latitude: Double, longitude: Double, uid: String) {
        viewModel.postChatBot(latitude, longitude, uid).observe(this) { res->
            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
                binding.progress.visibility = View.GONE
                return@observe
            } else {
                when(res){
                    is Result.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.progress.visibility = View.GONE
                        binding.root.showSnackbar(res.error)
                    }
                    is Result.Success -> {
                        binding.progress.visibility = View.GONE
                        val intent = Intent(this, ChatBotActivity::class.java)
                        startActivity(intent)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setupPieChart(anyChartView: AnyChartView) {
        val pie: Pie = AnyChart.pie()

        val data: List<DataEntry> = listOf(
            ValueDataEntry("A", result.clusterProportion?.a),
            ValueDataEntry("B", result.clusterProportion?.b),
            ValueDataEntry("C", result.clusterProportion?.c),
        )

        pie.data(data)

        anyChartView.setChart(pie)
    }

    companion object {
        const val RESULT = "result"
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
