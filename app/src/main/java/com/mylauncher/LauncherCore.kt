package com.mylauncher

import android.content.Context
import android.content.Intent
import dalvik.system.DexClassLoader
import java.io.File

object LauncherCore {

    fun launch(context: Context, apkPath: String): String {
        return try {
            val apkFile = File(apkPath)
            if (!apkFile.exists()) return "APK file not found at: $apkPath"

            val optimizedDir = context.getDir("dex_output", Context.MODE_PRIVATE)
            val nativeLibDir = context.getDir("native_libs", Context.MODE_PRIVATE)

            extractNativeLibs(apkPath, nativeLibDir.absolutePath)

            val classLoader = DexClassLoader(
                apkFile.absolutePath,
                optimizedDir.absolutePath,
                nativeLibDir.absolutePath,
                context.classLoader
            )

            val minecraftClass = classLoader.loadClass("com.mojang.minecraftpe.MainActivity")

            val intent = Intent(context, minecraftClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
            "Launching Minecraft..."

        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private fun extractNativeLibs(apkPath: String, outDir: String) {
        try {
            val zip = java.util.zip.ZipFile(apkPath)
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry.name.contains("lib/arm64-v8a") && entry.name.endsWith(".so")) {
                    val outFile = File(outDir, File(entry.name).name)
                    if (!outFile.exists()) {
                        zip.getInputStream(entry).use { input ->
                            outFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
            zip.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
