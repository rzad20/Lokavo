package com.lokavo.ui.detailAnalysis

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.charts.Pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.lokavo.databinding.ActivityDetailAnalysisBinding

class DetailAnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }

    private fun setupPieChart(anyChartView: AnyChartView) {
        val pie: Pie = AnyChart.pie()

        val data: List<DataEntry> = listOf(
            ValueDataEntry("Apples", 6371664),
            ValueDataEntry("Pears", 789622),
            ValueDataEntry("Bananas", 7216301),
            ValueDataEntry("Grapes", 1486621),
            ValueDataEntry("Oranges", 1200000)
        )

        pie.data(data)

        anyChartView.setChart(pie)
    }
}
