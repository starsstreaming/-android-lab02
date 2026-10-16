package com.example.androidlab02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val HTTPS_URL = "https://httpbin.org/get?room=A204"
private const val HTTP_URL = "http://httpbin.org/get?room=A204"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var result by remember { mutableStateOf("等待请求") }
                var busy by remember { mutableStateOf(false) }
                fun request(address: String) {
                    busy = true
                    result = "请求中：$address"
                    Thread {
                        val response = HttpDemo.fetch(address)
                        runOnUiThread {
                            result = response
                            busy = false
                        }
                    }.start()
                }
                Column(
                    Modifier.fillMaxSize().statusBarsPadding()
                        .navigationBarsPadding().padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("姓名：吕伟航")
                    Text("学号：202530224057")
                    Text("实验二 网络访问")
                    Spacer(Modifier.height(8.dp))
                    Button(
                        enabled = !busy,
                        onClick = { request(HTTPS_URL) }
                    ) { Text("HTTPS 请求") }
                    Button(
                        enabled = !busy,
                        onClick = { request(HTTP_URL) }
                    ) { Text("HTTP 请求") }
                    Spacer(Modifier.height(8.dp))
                    Text(result, fontSize = 12.sp)
                }
            }
        }
    }
}
