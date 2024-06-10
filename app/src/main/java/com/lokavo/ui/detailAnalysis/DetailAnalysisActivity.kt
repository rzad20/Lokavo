package com.lokavo.ui.detailAnalysis

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.lokavo.R
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.databinding.ActivityDetailAnalysisBinding

class DetailAnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailAnalysisBinding
    private lateinit var result: ModelingResultsResponse

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Detail"
        }

        result = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(RESULT, ModelingResultsResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(RESULT)
        } ?: ModelingResultsResponse()

        binding.txtSentimentCategory.text = result.summaryHeader
        binding.detailAnalysis.text = "${result.longInterpretation}"

        if (result.summaryHeader.equals("highly competitive", true)) {
            binding.iconCompetitive.setImageResource(R.drawable.iconhighly)
        } else if (result.summaryHeader.equals("fairly competitive", true)) {
            binding.iconCompetitive.setImageResource(R.drawable.iconfairly)
        }

        val anyChartView = binding.anyChartView
        setupPieChart(anyChartView)
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
