package com.forumber.tokencasestudy

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.forumber.tokencasestudy.databinding.ActivityLoginBinding
import com.forumber.tokencasestudy.databinding.ActivityPosactivityBinding
import kotlin.concurrent.thread

class POSActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPosactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPosactivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.buttonGenerateQRCode.setOnClickListener {
            thread {
                val returnContent =
                    QSYAPI.sendRequest("{\"totalReceiptAmount\": ${binding.inputAmount.text}}")

                if (returnContent != null) {
                    val QRContent = QRCode.getQrContentFromJson(returnContent)
                    if (QRContent != null) {
                        val finalBitmap = QRCode.getQrCodeBitmap(QRContent)
                        runOnUiThread { binding.imageQRCode.setImageBitmap(finalBitmap) }
                    }
                }
            }
        }
    }
}