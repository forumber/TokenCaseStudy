package com.forumber.tokencasestudy

import android.graphics.Bitmap
import android.graphics.Color
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

class QRCode {
    companion object {

        fun getQrContentFromJson(content:String): String? {
            val parser: Parser = Parser.default()
            val json: JsonObject = parser.parse(content.reader()) as JsonObject
            return json.string("QRdata")
        }

        fun getQrCodeBitmap(qrCodeContent: String): Bitmap {
            val size = 512 //pixels
            val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
            return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
                for (x in 0 until size) {
                    for (y in 0 until size) {
                        it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
            }
        }
    }
}