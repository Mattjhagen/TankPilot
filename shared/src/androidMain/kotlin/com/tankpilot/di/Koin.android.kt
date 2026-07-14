package com.tankpilot.di

import com.tankpilot.core.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory(get()) }
}
