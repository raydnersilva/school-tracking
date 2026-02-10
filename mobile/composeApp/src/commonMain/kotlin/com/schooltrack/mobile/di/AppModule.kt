package com.schooltrack.mobile.di

import com.schooltrack.mobile.data.network.ApiClient
import com.schooltrack.mobile.data.network.TokenManager
import org.koin.dsl.module

val appModule = module {
    single { TokenManager() }
    single { ApiClient(tokenProvider = { get<TokenManager>().getToken() }) }
}
