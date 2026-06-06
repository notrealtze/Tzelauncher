package com.mylauncher

import android.net.Uri
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
    var mcApkPath by remember { mutableStateOf("No APK selected") }
    var statusMessage by remember { mutableStateOf("") }

    val apkPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            mcApkPath = it.path ?: "Unknown path"
            statusMessage = "APK selected. Ready to launch."
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
                    text = "MyLauncher",
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Minecraft Bedrock Launcher",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Selected APK:", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mcApkPath,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { apkPicker.launch("application/vnd.android.package-archive") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import Minecraft APK")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        statusMessage = LauncherCore.launch(activity, mcApkPath)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = mcApkPath != "No APK selected"
                ) {
                    Text("Launch Minecraft")
                }

                if (statusMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = statusMessage,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
