package com.application.presence.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.AuthResponse
import com.application.presence.data.model.Profile
import com.application.presence.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.security.MessageDigest
import java.util.UUID
import com.application.presence.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val context: Context
) {

    val supabase = SupabaseClientProvider.client

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
    }

    fun signInWithGoogle(): Flow<AuthResponse> = flow {

        emit(AuthResponse.Loading)

        val hashedNonce = createNonce()

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setNonce(hashedNonce)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(context)

        try {

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)

            val googleIdToken = googleIdTokenCredential.idToken

            supabase.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
            }
            val userId = supabase.auth.currentUserOrNull()?.id!!

            val profile = supabase //profile gives userId and GmailId if account exist else gives null
                .from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<UserProfile>()
            val isNewUser = profile == null

            emit(AuthResponse.Success(isNewUser, supabase.auth.currentUserOrNull()?.email)) //We can use this to check is new user or not

        } catch (e: Exception) {
            emit(AuthResponse.Error(e.localizedMessage))
        }
    }

    suspend fun InsertProfile(profile: Profile){
        val supabase = SupabaseClientProvider.client

        supabase
            .from("profiles")
            .insert(profile)
    }

    //It is not local but we want this to store data locally
    suspend fun supabaseProfile(): Profile? {
        val user = supabase.auth.currentUserOrNull()
        return user?.email?.let { safeEmail ->

            supabase
                .from("profiles")
                .select {
                    filter { eq("gmail_id", safeEmail) }
                }
                .decodeSingleOrNull<Profile>()

        }
    }

    suspend fun AndroidIdReturnedByGmail(id:String): String {
        val existingProfiles = supabase.from("profiles")
            .select {
                filter { eq("device_fingerprint", id) }
            }
            .decodeSingleOrNull<Profile>() // We use decodeList() just in case, to avoid crashes
            Log.e("ExistingProfiles","Existing Profiles: ${existingProfiles}")
        return  existingProfiles?.gmail_id ?: ""
    }
}

