package com.forumber.tokencasestudy

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.forumber.tokencasestudy.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = FirebaseAuth.getInstance().currentUser!!.email

        binding.buttonGoToPOS.setOnClickListener{
            val intent = Intent(this, POSActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, 0)
        }

        binding.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.buttonHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                val QRImageLocation = data!!.getStringExtra("qrimagelocation")

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
                .setMessage("Do you want to pay ${QSYAPI.convertAmountToTL(transactionAmount)}?")
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
        if (QSYAPI.sendPaymentRequest(qrCodeContent, transactionAmount))
            Database.addPayment(transactionAmount)
        else
            AlertDialog.Builder(this)
                .setMessage("Error!")
                .setNeutralButton("OK") {dialog, which ->
                dialog.dismiss()
            }
                .create().show()
    }

}