package com.application.presence.Screen

import android.annotation.SuppressLint
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.application.presence.R
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.AuthResponse
import com.application.presence.viewmodel.AuthViewModel
import io.github.jan.supabase.auth.auth

@SuppressLint("HardwareIds")
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess:() -> Unit,
    onAuthSuccessButNewUser:() -> Unit
) {

    val authState by viewModel.authState.collectAsState()
    val returnedGmailId by viewModel.returnedGmailId.collectAsState()
    val context = LocalContext.current
    val supabase = SupabaseClientProvider.client
    val email = supabase.auth.currentUserOrNull()?.email
    val androidId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    LaunchedEffect(returnedGmailId) {
        viewModel.getReturnedAndorid(androidId)
    }


    LaunchedEffect(authState) {
        when (authState) {
            is AuthResponse.Success -> {
                val IsNewUser = (authState as? AuthResponse.Success)?.isNewUser
                if(IsNewUser == true){
                    if(email != returnedGmailId){
                        Toast.makeText(
                            context,
                            "This Device is Locked with: ${returnedGmailId}",
                            Toast.LENGTH_LONG
                        ).show()
                    }else{
                        onAuthSuccess()
                    }
                }else{
                    if(email != returnedGmailId){
                        Toast.makeText(
                            context,
                            "This Device is Locked with: ${returnedGmailId}",
                            Toast.LENGTH_LONG
                        ).show()
                    }else{
                        viewModel.SupabaseProfile()
                        onAuthSuccessButNewUser()
                    }
                }
            }

            is AuthResponse.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthResponse.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        GoogleSignInButton(
            onClick = {
                viewModel.loginWithGoogle()
            }
        )
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.8f),
        shape = RoundedCornerShape(10.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.google),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(10.dp))

        Text("Sign in with Google")
    }
}