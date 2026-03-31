package com.application.presence.repository

import android.util.Log
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.AttendanceRequest
import com.application.presence.data.model.Profile
import com.application.presence.data.state.ScannerSubmissionState
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.headers


@OptIn(InternalAPI::class)
class ScannerRepository{
    suspend fun verifyScannedQr(uniqueTag: String, scannedCode: String, studentRoll: String, userLatitude: Double, userLongitude: Double): ScannerSubmissionState {
        val supabase = SupabaseClientProvider.client
        return try {
            // 1. Create the data object
            val requestData = AttendanceRequest(uniqueTag, scannedCode, studentRoll, userLatitude, userLongitude)
            val currentSessionToken = supabase.auth.currentAccessTokenOrNull()

            supabase.functions.invoke("rapid-verify") {
                // 1. Manually attach the valid JWT to the request headers!
                if (currentSessionToken != null) {
                    headers {
                        append("Authorization", "Bearer $currentSessionToken")
                    }
                }

                // 2. Keep the JSON body exactly as we had it
                body = TextContent(
                    text = Json.encodeToString(requestData),
                    contentType = ContentType.Application.Json
                )
            }

            ScannerSubmissionState.Success

        } catch (e: RestException) {
            // This grabs ONLY the first short sentence (e.g., "Already marked present")
            // and throws away the 1,500 characters of HTTP garbage!
            val cleanError = e.message?.substringBefore("\n") ?: "Server rejected the request"
            ScannerSubmissionState.Error(cleanError)

        } catch (e: Exception) {
            val cleanNetworkError = e.message?.substringBefore("\n") ?: "Network error occurred"
            ScannerSubmissionState.Error(cleanNetworkError)
        }
    }

    suspend fun getProfile(): Profile? {
        val supabase = SupabaseClientProvider.client
        val email = supabase.auth.currentUserOrNull()?.email?: return null
        return try {
            supabase.from("profiles")
                .select {
                    filter {
                        eq("gmail_id", email)
                    }
                }
                .decodeSingleOrNull()
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}
