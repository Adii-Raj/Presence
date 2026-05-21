package com.application.presence.repository

import android.content.Context
import android.util.Log
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
        try {
            val supabase = SupabaseClientProvider.client
            supabase
                .from("eventList")
                .insert(eventDataClass)
        } catch (e: Exception) {
            Log.e("AddEventRepository", "Error inserting event: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateEvent(eventDataClass: EventDataClass) {
        try {
            val supabase = SupabaseClientProvider.client
            val eventId = eventDataClass.id ?: throw Exception("Event ID is missing")
            
            Log.d("AddEventRepository", "Attempting update for ID: $eventId")
            
            // We use the builder to set columns explicitly. 
            // This avoids sending the 'id' in the body which can trigger DB errors.
            supabase.from("eventList").update(
                {
                    set("Event_Name", eventDataClass.Event_Name)
                    set("Event_Date", eventDataClass.Event_Date)
                    set("Event_Time", eventDataClass.Event_Time)
                    set("Event_Location", eventDataClass.Event_Location)
                    set("Event_Description", eventDataClass.Event_Description)
                    set("Event_Organiser", eventDataClass.Event_Organiser)
                    set("Event_Image", eventDataClass.Event_Image)
                    set("Event_Note", eventDataClass.Event_Note)
                    set("coordinates", eventDataClass.coordinates)
                    set("coordinates_radius", eventDataClass.coordinates_radius)
                }
            ) {
                filter {
                    eq("id", eventId)
                }
            }
            Log.d("AddEventRepository", "Update successful for ID: $eventId")
        } catch (e: Exception) {
            Log.e("AddEventRepository", "Error updating event: ${e.message}", e)
            throw e
        }
    }

    suspend fun deleteEvent(eventId: String) {
        try {
            val supabase = SupabaseClientProvider.client
            Log.d("AddEventRepository", "Attempting delete for ID: $eventId")
            
            supabase.from("eventList").delete {
                filter {
                    eq("id", eventId)
                }
            }
            Log.d("AddEventRepository", "Delete successful for ID: $eventId")
        } catch (e: Exception) {
            Log.e("AddEventRepository", "Error deleting event: ${e.message}", e)
            throw e
        }
    }
}
