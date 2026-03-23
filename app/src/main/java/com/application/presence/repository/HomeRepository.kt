package com.application.presence.repository

import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.EventDataClass
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class HomeRepository {

    suspend fun getEvent(): List<EventDataClass>{
        val supabase = SupabaseClientProvider.client
        return supabase.from("eventList")
            .select()
            .decodeList<EventDataClass>()
    }


}