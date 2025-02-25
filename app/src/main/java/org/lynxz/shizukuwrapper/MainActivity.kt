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
        findViewById<Button>(R.id.btn_get_imei_fun1).setOnClickListener {
            val result = ShizukuImpl.getSystemProperty("ro.ril.oem.imei")
            Toast.makeText(this, "getSystemProperty('ro.ril.oem.imei')=$result", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btn_get_imei_fun2).setOnClickListener {
            val result = ShizukuImpl.getImei(0)
            Toast.makeText(this, "getImei(0)=$result", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btn_get_imei_fun2_1).setOnClickListener {
            val result = ShizukuImpl.getImei(1)
            Toast.makeText(this, "getImei(1)=$result", Toast.LENGTH_SHORT).show()
        }
    }
}

