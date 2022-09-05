package com.example.myfirstapplication

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blikoon.qrcodescanner.QrCodeActivity
import java.nio.file.attribute.AclEntry
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var REQUEST_CODE_QR_SCAN = 1880
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.scan_qr)

        btn.setOnClickListener {
            val i = Intent(this@MainActivity, QrCodeActivity::class.java)
            startActivityForResult(i, REQUEST_CODE_QR_SCAN)
        }

        tts = TextToSpeech(this, this)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            Log.d("LOGTAG", "COULD NOT GET A GOOD RESULT.")
            if (data == null) return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image")
            if (result != null) {
                val alertDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
                alertDialog.setTitle("Scan Error")
                alertDialog.setMessage("QR Code could not be scanned")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                alertDialog.show()
            }
            return
        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult")
            Log.d("LOGTAG", "Have scan result in your app activity :$result")
            val alertDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setTitle("Scan result")
            alertDialog.setMessage(result)
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
            speakOut(result.toString())
        }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                true
            }
        }
    }
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}