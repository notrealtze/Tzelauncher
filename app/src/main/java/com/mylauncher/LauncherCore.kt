package com.mylauncher

import android.content.Context
import android.content.Intent
import android.net.Uri
import dalvik.system.DexClassLoader
import java.io.File

object LauncherCore {

    fun launch(context: Context, apkPath: String): String {
        return try {
            val apkFile = File(apkPath)
            if (!apkFile.exists()) return "APK file not found"

            val optimizedDir = context.getDir("dex_output", Context.MODE_PRIVATE)

            val classLoader = DexClassLoader(
                apkFile.absolutePath,
                optimizedDir.absolutePath,
                null,
                context.classLoader
            )

            // Attempt to find and launch Minecraft main activity
            val minecraftClass = classLoader.loadClass(
                "com.mojang.minecraftpe.MainActivity"
            )

            val intent = Intent(context, minecraftClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Launching Minecraft..."

        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
