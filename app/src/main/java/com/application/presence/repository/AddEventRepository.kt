package com.application.presence.repository

import android.content.Context
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.EventDataClass
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AddEventRepository(private val context: Context) {
    suspend fun getOfflineMapFile(fileName: String): File = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, fileName)
        if (!file.exists() || file.length() == 0L) {
            context.assets.open(fileName).use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return@withContext file
    }


    suspend fun insertEvent(eventDataClass: EventDataClass){
        val supabase = SupabaseClientProvider.client
        supabase
            .from("eventList")
            .insert(eventDataClass)
    }
}