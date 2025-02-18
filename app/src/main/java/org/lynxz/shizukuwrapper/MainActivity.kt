package org.lynxz.shizukuwrapper

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import org.lynxz.shizuku.ShizukuImpl

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_get_serial_number).setOnClickListener {
            val result = ShizukuImpl.getSystemProperty("ro.serialno", "")
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_open_wifi).setOnClickListener {
            ShizukuImpl.exec("svc wifi enable")
        }
        findViewById<Button>(R.id.btn_close_wifi).setOnClickListener {
            ShizukuImpl.exec("svc wifi disable")
        }
    }
}

