package com.schooltrack.mobile

import androidx.compose.runtime.Composable
import com.schooltrack.mobile.data.network.TokenManager
import com.schooltrack.mobile.ui.navigation.AppNavigation
import com.schooltrack.mobile.ui.theme.SchoolTrackTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val tokenManager = koinInject<TokenManager>()

    SchoolTrackTheme {
        AppNavigation(tokenManager = tokenManager)
    }
}
