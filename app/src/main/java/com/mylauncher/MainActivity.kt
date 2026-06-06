package com.mylauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LauncherUI(this)
        }
    }
}

@Composable
fun LauncherUI(activity: MainActivity) {
    var statusMessage by remember { mutableStateOf("") }
    var minecraftInstalled by remember { mutableStateOf(MinecraftHelper.isMinecraftInstalled(activity)) }

    val resourceFilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            statusMessage = ResourceImporter.importFile(activity, it)
        }
    }

    val resourceDirPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            statusMessage = ResourceImporter.importDirectory(activity, it)
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Minecraft Bedrock Launcher",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (!minecraftInstalled) {
                    Text(
                        text = "Minecraft is not installed.\nPlease install it first.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        statusMessage = LauncherCore.launch(activity)
                        minecraftInstalled = MinecraftHelper.isMinecraftInstalled(activity)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = minecraftInstalled
                ) {
                    Text("Launch Minecraft")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { resourceFilePicker.launch("*/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = minecraftInstalled
                ) {
                    Text("Load Resource Pack / Addon")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { resourceDirPicker.launch(null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = minecraftInstalled
                ) {
                    Text("Load World / Template")
                }

                if (statusMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = statusMessage,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
