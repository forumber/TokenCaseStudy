package com.forumber.tokencasestudy

import android.graphics.Bitmap
import android.graphics.Color
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter


class QRCode {
    companion object {

        private fun bitmapToBinaryBitmap(bMap: Bitmap): BinaryBitmap {
            val intArray = IntArray(bMap.width * bMap.height)
            bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)

            val source: LuminanceSource =
                RGBLuminanceSource(bMap.width, bMap.height, intArray)
            return BinaryBitmap(HybridBinarizer(source))
        }

        fun readQrCode(qrCodeBitmap: Bitmap): String {
            return QRCodeReader().decode(bitmapToBinaryBitmap(qrCodeBitmap)).text
        }

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