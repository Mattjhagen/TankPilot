package com.tankpilot.android.di

import com.tankpilot.android.viewmodel.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel(get(), get(), get(), get()) }
}
