package com.application.presence.repository

import android.util.Log
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.EventDataClass
import com.application.presence.data.model.KeysDataClass
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class HomeRepository {

    suspend fun getEvent(): List<EventDataClass>{
        val supabase = SupabaseClientProvider.client
        val result = supabase.from("eventList")
            .select()
            .decodeList<EventDataClass>()
        return result
    }

    suspend fun getUniqueAndSecret(
        id:String
    ): KeysDataClass?
    {
        val supabase = SupabaseClientProvider.client
        val result = supabase.from("event_qr_keys")
            .select {
                filter {
                    eq("event_id", id)
                }
            }
            .decodeSingleOrNull<KeysDataClass>()
        return result
    }
}