package com.forumber.tokencasestudy

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.forumber.tokencasestudy.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.buttonGoToPOS.setOnClickListener{

            val intent = Intent(this, POSActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, 0)

            //startActivity(Intent(this, POSActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                val QRImageLocation = data!!.getStringExtra("qrimagelocation")
                Toast.makeText(this, QRImageLocation, Toast.LENGTH_SHORT).show()

                receiveQR(QRImageLocation)
            }
        } else {
            @Suppress("DEPRECATION")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun receiveQR(qrImageLocation: String?) {
        val theBitmap = BitmapFactory.decodeFile(qrImageLocation)

        val image = ImageView(this)
        image.setImageBitmap(theBitmap)

        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this)
                .setMessage("This QR code will be read:")
                .setPositiveButton("OK") {dialog, which ->
                    dialog.dismiss()
                    readQRCode(theBitmap)
                }
                .setView(image)
        builder.create().show()
    }

    private fun readQRCode(theBitmap: Bitmap) {
        val qrCodeContent = QRCode.readQrCode(theBitmap)
        val transactionAmount = QSYAPI.getTransactionAmount(qrCodeContent)

        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this)
                .setMessage("Do you want to pay $transactionAmount kuruş?")
                .setPositiveButton("Yes") {dialog, which ->
                    dialog.dismiss()
                    doPayment(qrCodeContent, transactionAmount)
                }
                .setNegativeButton("No") {dialog, which ->
                    dialog.dismiss()
                }
        builder.create().show()
    }

    private fun doPayment(qrCodeContent: String, transactionAmount: Int) {
        QSYAPI.sendPaymentRequest(qrCodeContent, transactionAmount)

    }


}