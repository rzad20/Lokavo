package com.lokavo.di

import com.lokavo.data.repository.HistoryRepository
import com.lokavo.data.repository.MapsRepository
import com.lokavo.data.local.room.LokavoDatabase
import com.lokavo.data.preferences.SwitchThemePreferences
import com.lokavo.data.remote.retrofit.ApiConfig
import com.lokavo.data.repository.ProfileRepository
import com.lokavo.ui.history.HistoryViewModel
import com.lokavo.ui.maps.MapsViewModel
import com.lokavo.ui.profile.ProfileViewModel
import com.lokavo.ui.result.ResultViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.Executors

val appModule = module {
    single { ApiConfig.getApiService() }
    single { MapsRepository.getInstance(get()) }
    single { Executors.newSingleThreadExecutor() }
    single { LokavoDatabase.getDatabase(androidApplication()) }
    single { get<LokavoDatabase>().historyDao() }
    single { HistoryRepository.getInstance(get(), get()) }
    single { SwitchThemePreferences.getInstance(androidApplication())}
    single { ProfileRepository.getInstance(get())}
    viewModel { ResultViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { MapsViewModel() }
    viewModel { ProfileViewModel(get()) }
}