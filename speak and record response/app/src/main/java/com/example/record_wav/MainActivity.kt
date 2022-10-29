package com.example.record_wav

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.record_wav.listener.TTSListener
import com.example.record_wav.util.TTS

class MainActivity : AppCompatActivity(), TTSListener {
    private val REQUEST_AUDIO_PERMISSION_CODE = 200
    lateinit var button: Button
    var flag:Boolean =false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RequestPermissions()
        if(SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")

                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        uri
                    )
                )
            }
        }
        TTS.init(applicationContext,this)
        button= findViewById(R.id.record)
        button.setOnClickListener(View.OnClickListener {
//            button.visibility = View.GONE
            Thread(Runnable {
                Thread.sleep(500)
                TTS.speakText("hi Bixby");
            }).start()

        })
    }

    override fun onPause() {
        super.onPause()
//        RecordUtil.stopRecordingByAR()
//        button.text="start"
    }

    fun CheckPermissions(): Boolean {
        // this method is used to check permission
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        val result3 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
    }

    private fun RequestPermissions() {
        if (!CheckPermissions()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_AUDIO_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE-> if (grantResults.size > 0) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val permissionToRead = grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore && permissionToRead) {
                } else {
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }



    override fun onFinish() {
//        button.visibility = View.VISIBLE
    }
}