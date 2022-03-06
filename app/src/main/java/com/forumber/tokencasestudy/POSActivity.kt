package com.forumber.tokencasestudy

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.view.drawToBitmap
import com.forumber.tokencasestudy.databinding.ActivityLoginBinding
import com.forumber.tokencasestudy.databinding.ActivityPosactivityBinding
import java.lang.Exception
import kotlin.concurrent.thread

class POSActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPosactivityBinding
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPosactivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "POS"

        binding.buttonGenerateQRCode.setOnClickListener {
            thread {
                try {
                    val returnContent =
                        QSYAPI.sendQRRequest(binding.inputAmount.text.toString().toInt())

                    val QRContent = QRCode.getQrContentFromJson(returnContent)
                    if (QRContent != null) {
                        val finalBitmap = QRCode.getQrCodeBitmap(QRContent)
                        runOnUiThread {
                            binding.imageQRCode.setImageBitmap(finalBitmap)
                            binding.buttonReadQROnCustomer.visibility = View.VISIBLE
                        }
                        saveBitmap(finalBitmap)
                    }
                } catch (e: Exception)
                {
                    runOnUiThread {AlertDialog.Builder(this)
                        .setMessage("Error!")
                        .setNeutralButton("OK") {dialog, which ->
                            dialog.dismiss()
                        }
                        .create().show()}
                }
            }
        }

        binding.buttonReadQROnCustomer.setOnClickListener {
            val intent = Intent().apply {
                putExtra("qrimagelocation", "$filesDir/bitmap.jpg")
            }
            setResult(Activity.RESULT_OK, intent)
            onBackPressed()
        }
    }

    private fun saveBitmap(theBitmap: Bitmap) {
        val theOutput = openFileOutput("bitmap.jpg", Context.MODE_PRIVATE)
        theBitmap.compress(Bitmap.CompressFormat.JPEG, 100, theOutput)
        theOutput.flush()
        theOutput.close()
    }
}