package org.lynxz.shizukuwrapper

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.lynxz.shizuku.ShizukuImpl
import org.lynxz.shizukuwrapper.ui.theme.ShizukuWrapperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShizukuWrapperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )

        Button(onClick = { ShizukuImpl.getSystemProperty("ro.serialno", "") }) { Text(text = "获取设备序列号") }
        Button(onClick = { ShizukuImpl.exec("svc wifi enable") }) { Text(text = "开启wifi") }
        Button(onClick = { ShizukuImpl.exec("svc wifi disable") }) { Text(text = "关闭wifi") }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShizukuWrapperTheme {
        Greeting("Android")
    }
}