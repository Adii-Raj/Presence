package com.application.presence.repository

import android.util.Log
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.EventDataClass
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class HomeRepository {

    suspend fun getEvent(): List<EventDataClass>{
        val supabase = SupabaseClientProvider.client
        val result = supabase.from("eventList")
            .select()
            .decodeList<EventDataClass>()
        Log.e("Repository", result.toString())
        return result
    }


}