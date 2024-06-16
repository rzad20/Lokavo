package com.lokavo.ui.detailAnalysis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.lokavo.data.Result
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.R
import com.lokavo.data.remote.response.ChatBotMessageResponse
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.databinding.ActivityDetailAnalysisBinding
import com.lokavo.ui.adapter.TopCompetitorAdapter
import com.lokavo.ui.chatbot.ChatBotActivity
import com.lokavo.ui.placeDetail.PlaceDetailActivity
import com.lokavo.ui.result.ResultViewModel
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailAnalysisActivity : AppCompatActivity(), TopCompetitorAdapter.OnItemClickListener {
    private lateinit var binding: ActivityDetailAnalysisBinding
    private lateinit var result: ModelingResultsResponse
    private val viewModel: DetailAnalysisViewModel by viewModel()
    private val detailViewModel: ResultViewModel by viewModel()
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        result = intent.getParcelableExtra(RESULT) ?: ModelingResultsResponse()

        binding.txtSentimentCategory.text = result.summaryHeader
        binding.detailAnalysis.text = result.longInterpretation

        recyclerView = binding.rvCompetitor
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TopCompetitorAdapter(result.top, this)


        when (result.summaryHeader?.lowercase()) {
            "highly competitive" -> binding.iconCompetitive.setImageResource(R.drawable.iconhighly)
            "fairly competitive" -> binding.iconCompetitive.setImageResource(R.drawable.iconfairly)
        }

        val anyChartView = binding.anyChartView
        setupPieChart(anyChartView)

        binding.chatbotNavigation.setOnClickListener {
            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
            } else {
                result.latLng?.let {
                    if (uid != null) {
                        postChatBot(it.latitude, it.longitude, uid)
                    }
                }
            }
        }
    }

    private fun postChatBot(latitude: Double, longitude: Double, uid: String) {
        viewModel.postChatBot(latitude, longitude, uid).observe(this) { res ->
            when (res) {
                is Result.Loading -> {
                    showLoading()
                }

                is Result.Error -> {
                    hideLoading()
                    binding.root.showSnackbar(res.error)
                }

                is Result.Success -> {
                    fetchChatBotMessages(uid, latitude, longitude)
                }

                else -> {}
            }
        }
    }

    private fun fetchChatBotMessages(uid: String, latitude: Double, longitude: Double) {
        val messages = mutableListOf<ChatBotMessageResponse>()
        val indices = listOf(1, 2, 3)

        val messageFetchers = indices.map { index ->
            viewModel.getChatBotMessage(uid, index)
        }

        messageFetchers.forEachIndexed { _, liveData ->
            liveData.observe(this) { res ->
                when (res) {
                    is Result.Loading -> {
                      showLoading()
                    }

                    is Result.Error -> {
                        hideLoading()
                        binding.root.showSnackbar(res.error)
                    }

                    is Result.Success -> {
                        hideLoading()
                        val message = ChatBotMessageResponse(
                            answer = res.data.answer,
                            question = res.data.question,
                        )
                        messages.add(message)
                        if (messages.size == indices.size) {
                            navigateToChatBotActivity(latitude, longitude, messages)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun navigateToChatBotActivity(
        latitude: Double,
        longitude: Double,
        messages: List<ChatBotMessageResponse>
    ) {
        val intent = Intent(this, ChatBotActivity::class.java).apply {
            putExtra(ChatBotActivity.LOCATION, LatLng(latitude, longitude))
            putParcelableArrayListExtra(ChatBotActivity.MESSAGES, ArrayList(messages))
        }
        startActivity(intent)
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

    override fun onItemClick(placeId: String, button: Button) {
        detailViewModel.getPlaceDetail(placeId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading()
                    button.isEnabled = false
                }

                is Result.Success -> {
                    hideLoading()
                    button.isEnabled = true
                    val detail = result.data
                    val moveWithObjectIntent = Intent(this, PlaceDetailActivity::class.java)
                    moveWithObjectIntent.putExtra(PlaceDetailActivity.RESULT, detail)
                    startActivity(moveWithObjectIntent)
                }

                is Result.Error -> {
                    hideLoading()
                    button.isEnabled = true
                    binding.root.showSnackbar(result.error)
                }

                is Result.Empty -> {
                    hideLoading()
                    button.isEnabled = true
                    binding.root.showSnackbar(getString(R.string.not_found))
                }

                null -> {}
            }
        }
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.chatbotNavigation.isEnabled = false
    }

    private fun hideLoading() {
        binding.progress.visibility = View.GONE
        binding.chatbotNavigation.isEnabled = true
    }

}
