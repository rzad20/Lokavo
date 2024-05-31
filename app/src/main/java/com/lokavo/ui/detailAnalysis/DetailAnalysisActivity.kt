package com.lokavo.ui.detailAnalysis

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.lokavo.R
import com.lokavo.databinding.ActivityDetailAnalysisBinding

class DetailAnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupPieChart()
    }

    private fun setupPieChart() {
        val anyChartView = binding.anyChartView
        val pie = AnyChart.pie()

        pie.setOnClickListener(object : ListenersInterface.OnClickListener(arrayOf("x", "value")) {
            override fun onClick(event: Event) {
                Toast.makeText(
                    this@DetailAnalysisActivity,
                    "${event.data["x"]}: ${event.data["value"]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Define data entries
        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Cluster 1", 40))
        data.add(ValueDataEntry("Cluster 2", 30))
        data.add(ValueDataEntry("Cluster 3", 30))

        // Set data
        pie.data(data)

        // Set colors
        pie.palette(arrayOf("#4CAF50", "#FFC107", "#F44336")) // Green, Yellow, Red

        pie.title("Best to Worst Clustering")

        pie.labels().position("outside")

        pie.legend().title().enabled(true)
        pie.legend().title().text("Clusters").padding(0.0, 0.0, 10.0, 0.0)
        pie.legend().position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)

        anyChartView.setChart(pie)
    }
}
