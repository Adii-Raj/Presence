package com.application.presence.repository

import android.content.Context
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
}