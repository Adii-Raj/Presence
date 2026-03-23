package com.application.presence.Screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.presence.R
import com.application.presence.data.state.SplashState
import com.application.presence.viewmodel.SplashViewModel
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDetail : () -> Unit
) {
    // Observe the state from ViewModel
    val splashState by viewModel.splashState.collectAsState()

    // Animation states
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    // Trigger animations when the composable enters the composition
    LaunchedEffect(key1 = true) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }

    // Handle Navigation based on ViewModel state
    LaunchedEffect(key1 = splashState) {
        when (splashState) {
            is SplashState.NavigateToLogin -> onNavigateToLogin()
            is SplashState.NavigateToHome -> onNavigateToHome()
            is SplashState.NavigateToDetail -> onNavigateToDetail()
            SplashState.Loading -> { /* Keep showing splash */ }
        }
    }

    // Minimalist UI Design
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // 1. Center Content (Logo and App Name)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {

            Surface(
                modifier = Modifier
                    .size(120.dp),
                color = Color.Transparent
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ptulogo),
                    contentDescription = "College Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PRESENCE",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

        }

        // 2. Bottom Content (Progress Indicator and Creator Text)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp), // Slightly reduced bottom padding to fit both nicely
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .alpha(alpha.value),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )

                Spacer(modifier = Modifier.height(16.dp)) // Space between the loader and the text

                Text(
                    text = "Made by Aditya",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.alpha(alpha.value), // Added alpha so it fades in too
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}