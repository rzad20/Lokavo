package com.lokavo.di

import com.lokavo.data.MapsRepository
import com.lokavo.data.retrofit.ApiConfig
import com.lokavo.ui.ResultViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { ApiConfig.getApiService() }
    single { MapsRepository.getInstance(get()) }
    viewModel { ResultViewModel(get()) }
}
