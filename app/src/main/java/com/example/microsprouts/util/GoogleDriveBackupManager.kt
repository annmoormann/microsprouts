package com.example.microsprouts.util

import android.content.Context
import com.example.microsprouts.data.database.MicroSproutsDatabase
import com.example.microsprouts.data.entity.Category
import com.example.microsprouts.data.entity.Task
import com.example.microsprouts.data.entity.TaskCategoryCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class AppBackupPayload(
    val tasks: List<Task>,
    val categories: List<Category>,
    val crossRefs: List<TaskCategoryCrossRef>
)

class GoogleDriveBackupManager(private val context: Context, private val database: MicroSproutsDatabase) {

    /**
     * Serializes all tasks, categories, and relations into a local JSON backup file.
     */
    suspend fun createLocalBackupFile(): File = withContext(Dispatchers.IO) {
        val tasks = database.taskDao().getAllTasksRaw()
        val categories = database.taskDao().getAllCategoriesRaw()
        val crossRefs = database.taskDao().getAllCrossRefsRaw()

        val jsonRoot = JSONObject().apply {
            put("version", 1)
            put("timestamp", System.currentTimeMillis())

            // Serialize Tasks
            val tasksArray = JSONArray()
            tasks.forEach { task ->
                tasksArray.put(JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("description", task.description)
                    put("isCompleted", task.isCompleted)
                    put("currentList", task.currentList.name)
                    put("isRecurring", task.isRecurring)
                    put("intervalDays", task.intervalDays)
                    put("recurrenceBehavior", task.recurrenceBehavior.name)
                    put("lastGeneratedTimestamp", task.lastGeneratedTimestamp)
                    put("recurrenceUnit", task.recurrenceUnit.name)
                    put("intervalValue", task.intervalValue)
                    put("monthlyRuleType", task.monthlyRuleType.name)
                    put("monthlyDayOfMonth", task.monthlyDayOfMonth)
                    put("yearlyRuleType", task.yearlyRuleType.name)
                    put("yearlyMonth", task.yearlyMonth)
                    put("yearlyDayOfMonth", task.yearlyDayOfMonth)
                    put("parentId", task.parentId ?: JSONObject.NULL)
                    put("primaryCategoryId", task.primaryCategoryId ?: JSONObject.NULL)
                })
            }
            put("tasks", tasksArray)

            // Serialize Categories
            val catArray = JSONArray()
            categories.forEach { cat ->
                catArray.put(JSONObject().apply {
                    put("id", cat.id)
                    put("name", cat.name)
                    put("colorHex", cat.colorHex)
                })
            }
            put("categories", catArray)

            // Serialize CrossRefs
            val refArray = JSONArray()
            crossRefs.forEach { ref ->
                refArray.put(JSONObject().apply {
                    put("taskId", ref.taskId)
                    put("categoryId", ref.categoryId)
                })
            }
            put("crossRefs", refArray)
        }

        val backupFile = File(context.cacheDir, "microsprouts_backup.json")
        FileOutputStream(backupFile).use { fos ->
            fos.write(jsonRoot.toString(2).toByteArray())
        }
        backupFile
    }

    /**
     * Restores database content from a parsed JSON payload file.
     */
    suspend fun restoreFromBackupFile(file: File) = withContext(Dispatchers.IO) {
        val fileReader = FileReader(file)
        val jsonString = fileReader.readText()
        fileReader.close()

        val jsonRoot = JSONObject(jsonString)
        
        // Clear existing data safely
        database.taskDao().clearAllCrossRefs()
        database.taskDao().clearAllTasks()
        database.taskDao().clearAllCategories()

        // Restore Categories first
        val catArray = jsonRoot.getJSONArray("categories")
        for (i in 0 until catArray.length()) {
            val obj = catArray.getJSONObject(i)
            database.taskDao().insertCategoryRaw(
                Category(
                    id = obj.getLong("id"),
                    name = obj.getString("name"),
                    colorHex = obj.getString("colorHex")
                )
            )
        }

        // Restore Tasks
        val tasksArray = jsonRoot.getJSONArray("tasks")
        for (i in 0 until tasksArray.length()) {
            val obj = tasksArray.getJSONObject(i)
            database.taskDao().insertTaskRaw(
                Task(
                    id = obj.getLong("id"),
                    title = obj.getString("title"),
                    description = obj.optString("description", ""),
                    isCompleted = obj.getBoolean("isCompleted"),
                    currentList = com.example.microsprouts.data.entity.TaskList.valueOf(obj.getString("currentList")),
                    isRecurring = obj.getBoolean("isRecurring"),
                    intervalDays = obj.getInt("intervalDays"),
                    recurrenceBehavior = com.example.microsprouts.data.entity.RecurrenceBehavior.valueOf(obj.getString("recurrenceBehavior")),
                    lastGeneratedTimestamp = obj.getLong("lastGeneratedTimestamp"),
                    recurrenceUnit = com.example.microsprouts.data.entity.RecurrenceUnit.valueOf(obj.getString("recurrenceUnit")),
                    intervalValue = obj.getInt("intervalValue"),
                    monthlyRuleType = com.example.microsprouts.data.entity.MonthlyRuleType.valueOf(obj.getString("monthlyRuleType")),
                    monthlyDayOfMonth = obj.getInt("monthlyDayOfMonth"),
                    yearlyRuleType = com.example.microsprouts.data.entity.YearlyRuleType.valueOf(obj.getString("yearlyRuleType")),
                    yearlyMonth = obj.getInt("yearlyMonth"),
                    yearlyDayOfMonth = obj.getInt("yearlyDayOfMonth"),
                    parentId = if (obj.isNull("parentId")) null else obj.getLong("parentId"),
                    primaryCategoryId = if (obj.isNull("primaryCategoryId")) null else obj.getLong("primaryCategoryId")
                )
            )
        }

        // Restore CrossRefs
        val refArray = jsonRoot.getJSONArray("crossRefs")
        for (i in 0 until refArray.length()) {
            val obj = refArray.getJSONObject(i)
            database.taskDao().insertCrossRefRaw(
                TaskCategoryCrossRef(
                    taskId = obj.getLong("taskId"),
                    categoryId = obj.getLong("categoryId")
                )
            )
        }
    }

    /**
     * Uploads the local backup file to Google Drive.
     */
    suspend fun uploadBackupToDrive(credential: GoogleAccountCredential, fileToUpload: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService(credential)

            // Check if file already exists in AppData folder
            val fileList = driveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("files(id, name)")
                .execute()
            
            var existingFileId: String? = null
            for (file in fileList.files) {
                if (file.name == "microsprouts_backup.json") {
                    existingFileId = file.id
                    break
                }
            }

            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = "microsprouts_backup.json"
                if (existingFileId == null) {
                    parents = listOf("appDataFolder")
                }
            }
            val mediaContent = FileContent("application/json", fileToUpload)

            if (existingFileId != null) {
                // Update existing file
                driveService.files().update(existingFileId, fileMetadata, mediaContent).execute()
            } else {
                // Create new file
                driveService.files().create(fileMetadata, mediaContent).execute()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Downloads the backup file from Google Drive to the local cache.
     */
    suspend fun downloadBackupFromDrive(credential: GoogleAccountCredential): File? = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService(credential)

            // Find the file in AppData folder
            val fileList = driveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("files(id, name)")
                .execute()
            
            var fileId: String? = null
            for (file in fileList.files) {
                if (file.name == "microsprouts_backup.json") {
                    fileId = file.id
                    break
                }
            }

            if (fileId != null) {
                val outputFile = File(context.cacheDir, "microsprouts_backup_downloaded.json")
                FileOutputStream(outputFile).use { outputStream ->
                    driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream)
                }
                return@withContext outputFile
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getDriveService(credential: GoogleAccountCredential): Drive {
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("MicroSprouts").build()
    }
}