package com.mylauncher

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object PackInstaller {

    private const val MINECRAFT_BASE_PATH = "Android/data/com.mojang.minecraftpe/files/games/com.mojang"

    fun importFile(context: Context, fileUri: Uri, fileName: String): String {
        return try {
            val targetFolder = when {
                fileName.endsWith(".mcpack", ignoreCase = true) -> "resource_packs"
                fileName.endsWith(".mcworld", ignoreCase = true) -> "minecraftWorlds"
                fileName.endsWith(".mcaddon", ignoreCase = true) -> "resource_packs"
                else -> return "Unsupported file format. Please choose an mcpack, mcworld, or mcaddon."
            }

            val storageDir = File(android.os.Environment.getExternalStorageDirectory(), MINECRAFT_BASE_PATH)
            val destinationDir = File(storageDir, targetFolder)

            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
            }

            val packName = fileName.substringBeforeLast(".")
            val outputExtractionDir = File(destinationDir, packName)
            if (!outputExtractionDir.exists()) {
                outputExtractionDir.mkdirs()
            }

            context.contentResolver.openInputStream(fileUri).use { inputStream ->
                if (inputStream == null) return "Failed to read the selected file."
                
                ZipInputStream(inputStream).use { zipStream ->
                    var entry = zipStream.nextEntry
                    while (entry != null) {
                        val outFile = File(outputExtractionDir, entry.name)
                        
                        if (entry.isDirectory) {
                            outFile.mkdirs()
                        } else {
                            outFile.parentFile?.mkdirs()
                            FileOutputStream(outFile).use { outputStream ->
                                zipStream.copyTo(outputStream)
                            }
                        }
                        zipStream.closeEntry()
                        entry = zipStream.nextEntry
                    }
                }
            }

            "Successfully imported $packName into $targetFolder instantly!"

        } catch (e: Exception) {
            "Import failed: ${e.message}"
        }
    }
}
