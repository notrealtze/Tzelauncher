package com.mylauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import java.io.File

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
    fun importFile(context: Context, uri: Uri): String {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(uri))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "File sent to Minecraft for import"
        } catch (e: Exception) {
            "Import error: ${e.message}"
        }
    }

    fun importWorldOrTemplate(context: Context, uri: Uri): String {
        return importFile(context, uri)
    }

    private fun getMimeType(uri: Uri): String {
        return when {
            uri.toString().endsWith(".mcpack") -> "application/octet-stream"
            uri.toString().endsWith(".mcaddon") -> "application/octet-stream"
            uri.toString().endsWith(".mcworld") -> "application/octet-stream"
            uri.toString().endsWith(".mctemplate") -> "application/octet-stream"
            uri.toString().endsWith(".zip") -> "application/zip"
            else -> "*/*"
        }
    }
}
