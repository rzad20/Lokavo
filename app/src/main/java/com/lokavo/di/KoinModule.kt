package com.lokavo.di

import com.lokavo.data.repository.HistoryRepository
import com.lokavo.data.repository.MapsRepository
import com.lokavo.data.local.room.LokavoDatabase
import com.lokavo.data.preferences.SwitchThemePreferences
import com.lokavo.data.remote.retrofit.ApiConfig1
import com.lokavo.data.remote.retrofit.ApiConfig2
import com.lokavo.data.repository.ChatBotRepository
import com.lokavo.data.repository.ProfileRepository
import com.lokavo.ui.chatbot.ChatBotViewModel
import com.lokavo.ui.detailAnalysis.DetailAnalysisViewModel
import com.lokavo.ui.history.HistoryViewModel
import com.lokavo.ui.maps.MapsViewModel
import com.lokavo.ui.profile.ProfileViewModel
import com.lokavo.ui.result.ResultViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.Executors

val appModule = module {
    single { ApiConfig1.getApiService() }
    single { ApiConfig2.getApiService() }
    single { MapsRepository.getInstance(get()) }
    single { ChatBotRepository.getInstance(get()) }
    single { Executors.newSingleThreadExecutor() }
    single { LokavoDatabase.getDatabase(androidApplication()) }
    single { get<LokavoDatabase>().historyDao() }
    single { HistoryRepository.getInstance(get(), get()) }
    single { SwitchThemePreferences.getInstance(androidApplication())}
    single { ProfileRepository.getInstance(get())}
    viewModel { ResultViewModel(get()) }
    viewModel { DetailAnalysisViewModel(get()) }
    viewModel { ChatBotViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { MapsViewModel() }
    viewModel { ProfileViewModel(get()) }
}