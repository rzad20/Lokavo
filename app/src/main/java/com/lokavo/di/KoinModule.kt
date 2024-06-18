package com.lokavo.di

import com.lokavo.data.repository.AnalyzeHistoryRepository
import com.lokavo.data.repository.MapsRepository
import com.lokavo.data.local.room.LokavoDatabase
import com.lokavo.data.remote.retrofit.ApiConfig1
import com.lokavo.data.remote.retrofit.ApiConfig2
import com.lokavo.data.repository.ArticleRepository
import com.lokavo.data.repository.ChatBotHistoryRepository
import com.lokavo.data.repository.ChatBotRepository
import com.lokavo.ui.article.ArticleViewModel
import com.lokavo.ui.detailAnalysis.DetailAnalysisViewModel
import com.lokavo.ui.analyzeHistory.AnalyzeHistoryViewModel
import com.lokavo.ui.chatBotHistory.ChatBotHistoryViewModel
import com.lokavo.ui.maps.MapsViewModel
import com.lokavo.ui.result.ResultViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.Executors

val appModule = module {
    single { ApiConfig1.getApiService() }
    single { ApiConfig2.getApiService() }

    single { Executors.newSingleThreadExecutor() }
    single { LokavoDatabase.getDatabase(androidApplication()) }
    single { get<LokavoDatabase>().analyzeHistoryDao() }
    single { get<LokavoDatabase>().chatBotHistoryDao() }

    single { MapsRepository.getInstance(get()) }
    single { ChatBotRepository.getInstance(get()) }
    single { ArticleRepository.getInstance(get()) }
    single { AnalyzeHistoryRepository.getInstance(get(), get()) }
    single { ChatBotHistoryRepository.getInstance(get(), get()) }


    viewModel { ResultViewModel(get()) }
    viewModel { DetailAnalysisViewModel(get()) }
    viewModel { AnalyzeHistoryViewModel(get()) }
    viewModel { ChatBotHistoryViewModel(get()) }
    viewModel { MapsViewModel() }
    viewModel { ArticleViewModel(get()) }
}