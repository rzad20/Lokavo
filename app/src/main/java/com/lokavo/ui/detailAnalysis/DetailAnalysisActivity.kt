package com.lokavo.ui.detailAnalysis

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
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
import com.lokavo.data.Result
import com.lokavo.data.remote.response.ChatBotMessageResponse
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.databinding.ActivityDetailAnalysisBinding
import com.lokavo.ui.adapter.TopCompetitorAdapter
import com.lokavo.ui.chatbot.ChatBotActivity
import com.lokavo.ui.placeDetail.PlaceDetailActivity
import com.lokavo.ui.result.ResultViewModel
import com.lokavo.utils.capitalizeWords
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

        binding.txtSentimentCategory.text = result.summaryHeader?.capitalizeWords()
        binding.detailAnalysis.text = result.longInterpretation

        result.clusterInterpretation?.let { clusterInterpretation ->
            binding.clusterMeaning1.text = clusterInterpretation.a
            binding.clusterMeaning2.text = clusterInterpretation.b
            binding.clusterMeaning3.text = clusterInterpretation.c

            binding.clusterMeaning1.visibility =
                if (clusterInterpretation.a.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.clusterMeaning2.visibility =
                if (clusterInterpretation.b.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.clusterMeaning3.visibility =
                if (clusterInterpretation.c.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        binding.clusterTitle.visibility =
            if (result.clusterInterpretation == null) View.GONE else View.VISIBLE
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
                    fetchChatBotMessagesSequentially(uid, latitude, longitude)
                }

                else -> {}
            }
        }
    }

    private fun fetchChatBotMessagesSequentially(uid: String, latitude: Double, longitude: Double) {
        val messages = mutableListOf<ChatBotMessageResponse>()

        fetchChatBotMessage(uid, 1).observe(this) { res1 ->
            when (res1) {
                is Result.Success -> {
                    messages.add(
                        ChatBotMessageResponse(
                            answer = res1.data.answer,
                            question = res1.data.question,
                        )
                    )
                    fetchChatBotMessage(uid, 2).observe(this) { res2 ->
                        when (res2) {
                            is Result.Success -> {
                                messages.add(
                                    ChatBotMessageResponse(
                                        answer = res2.data.answer,
                                        question = res2.data.question,
                                    )
                                )
                                fetchChatBotMessage(uid, 3).observe(this) { res3 ->
                                    when (res3) {
                                        is Result.Success -> {
                                            messages.add(
                                                ChatBotMessageResponse(
                                                    answer = res3.data.answer,
                                                    question = res3.data.question,
                                                )
                                            )
                                            hideLoading()
                                            navigateToChatBotActivity(latitude, longitude, messages)
                                        }

                                        is Result.Error -> {
                                            hideLoading()
                                            binding.root.showSnackbar(res3.error)
                                        }

                                        else -> {
                                            hideLoading()
                                        }
                                    }
                                }
                            }

                            is Result.Error -> {
                                hideLoading()
                                binding.root.showSnackbar(res2.error)
                            }

                            else -> {
                                hideLoading()
                            }
                        }
                    }
                }

                is Result.Error -> {
                    hideLoading()
                    binding.root.showSnackbar(res1.error)
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }

    private fun fetchChatBotMessage(
        uid: String,
        index: Int
    ): LiveData<Result<ChatBotMessageResponse>?> {
        return viewModel.getChatBotMessage(uid, index)
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

        val data = mutableListOf<DataEntry>()
        val colors = mutableListOf<String>()

        result.clusterProportion?.let {
            if (it.a!! > 0) {
                data.add(ValueDataEntry("A", it.a))
                colors.add("#C4A775")
            }
            if (it.b!! > 0) {
                data.add(ValueDataEntry("B", it.b))
                colors.add("#75C4A7")
            }
            if (it.c!! > 0) {
                data.add(ValueDataEntry("C", it.c))
                colors.add("#A775C4")
            }
        }

        pie.data(data)
        pie.palette(colors.toTypedArray())

        anyChartView.setChart(pie)
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

    companion object {
        const val RESULT = "result"
    }
}
