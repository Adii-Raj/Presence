package com.application.presence.repository

import android.util.Log
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.EventDataClass
import com.application.presence.data.model.KeysDataClass
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class HomeRepository {

    suspend fun getEvent(): List<EventDataClass>{
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("eventList")
                .select()
                .decodeList<EventDataClass>()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error fetching events: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUniqueAndSecret(
        id:String
    ): KeysDataClass?
    {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("event_qr_keys")
                .select {
                    filter {
                        eq("event_id", id)
                    }
                }
                .decodeSingleOrNull<KeysDataClass>()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error fetching keys: ${e.message}")
            null
        }
    }
}
