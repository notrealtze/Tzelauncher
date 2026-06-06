package com.mylauncher

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object MinecraftHelper {
    fun isMinecraftInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.mojang.minecraftpe", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}

object ResourceImporter {
    private fun getMinecraftBaseDir(): File {
        return File(Environment.getExternalStorageDirectory(), "games/com.mojang")
    }

    fun importFile(context: Context, uri: Uri): String {
        return try {
            val fileName = DocumentFile.fromSingleUri(context, uri)?.name
                ?: return "Could not read file name"
            val extension = fileName.substringAfterLast('.', "").lowercase()
            val baseDir = getMinecraftBaseDir()

            when (extension) {
                "mcpack" -> {
                    val destDir = File(baseDir, "resource_packs")
                    if (!destDir.exists()) destDir.mkdirs()
                    copyAndExtractZip(context, uri, destDir)
                    "Resource pack installed successfully"
                }
                "mcaddon" -> {
                    val destDir = File(baseDir, "behavior_packs")
                    if (!destDir.exists()) destDir.mkdirs()
                    copyAndExtractZip(context, uri, destDir)
                    "Addon installed successfully"
                }
                "mcworld" -> {
                    val destDir = File(baseDir, "minecraftWorlds")
                    if (!destDir.exists()) destDir.mkdirs()
                    copyAndExtractZip(context, uri, destDir)
                    "World imported successfully"
                }
                "mctemplate" -> {
                    val destDir = File(baseDir, "templates")
                    if (!destDir.exists()) destDir.mkdirs()
                    copyAndExtractZip(context, uri, destDir)
                    "Template installed successfully"
                }
                "zip" -> {
                    val destDir = File(baseDir, "resource_packs")
                    if (!destDir.exists()) destDir.mkdirs()
                    copyAndExtractZip(context, uri, destDir)
                    "Zip extracted to resource packs (verify type)"
                }
                else -> "Unsupported file type: .$extension"
            }
        } catch (e: Exception) {
            "Import error: ${e.message}"
        }
    }

    fun importDirectory(context: Context, treeUri: Uri): String {
        return try {
            val documentFile = DocumentFile.fromTreeUri(context, treeUri)
                ?: return "Cannot access folder"
            val folderName = documentFile.name ?: "imported_world"
            val baseDir = getMinecraftBaseDir()
            val worldDir = File(baseDir, "minecraftWorlds/$folderName")
            if (!worldDir.exists()) worldDir.mkdirs()

            copyDocumentDirectory(context, documentFile, worldDir)
            "World folder imported successfully"
        } catch (e: Exception) {
            "Import error: ${e.message}"
        }
    }

    private fun copyAndExtractZip(context: Context, uri: Uri, destDir: File) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val entryFile = File(destDir, entry.name)
                        entryFile.parentFile?.mkdirs()
                        FileOutputStream(entryFile).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
    }

    private fun copyDocumentDirectory(
        context: Context,
        sourceDir: DocumentFile,
        destDir: File
    ) {
        destDir.mkdirs()
        val files = sourceDir.listFiles()
        for (file in files) {
            val fileName = file.name ?: continue
            if (file.isDirectory) {
                copyDocumentDirectory(context, file, File(destDir, fileName))
            } else {
                val targetFile = File(destDir, fileName)
                context.contentResolver.openInputStream(file.uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }
}
