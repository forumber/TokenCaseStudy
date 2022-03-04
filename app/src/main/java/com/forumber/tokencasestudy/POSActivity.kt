package com.forumber.tokencasestudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.forumber.tokencasestudy.databinding.ActivityLoginBinding
import com.forumber.tokencasestudy.databinding.ActivityPosactivityBinding

class POSActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPosactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPosactivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.buttonGenerateQRCode.setOnClickListener {
            QSYAPI.sendRequest("{\"totalReceiptAmount\": 100}")
        }
    }
}