package com.application.presence.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import com.application.presence.BuildConfig
import io.github.jan.supabase.functions.Functions

object SupabaseClientProvider{
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ){
        install(Postgrest)
        install(Auth)
        install(Functions)
    }
}