package com.mylauncher

import android.content.Context
import android.content.Intent

object LauncherCore {
    fun launch(context: Context): String {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage("com.mojang.minecraftpe")
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                "Launching Minecraft..."
            } else {
                "Minecraft is not installed. Please install it first."
            }
        } catch (e: Exception) {
            "Error launching: ${e.message}"
        }
    }
}
