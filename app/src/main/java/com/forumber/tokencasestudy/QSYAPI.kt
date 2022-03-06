package com.forumber.tokencasestudy

import android.annotation.SuppressLint
import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.concurrent.thread

class QSYAPI {
    companion object {
        private fun ignoreSSL()
        {
            val trustAllCerts: Array<TrustManager> =
                arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    val acceptedIssuers: Array<Any?>?
                        get() = null

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
                        // Not implemented
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
                        // Not implemented
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        TODO("Not yet implemented")
                    }
                })

            try {
                val sc: SSLContext = SSLContext.getInstance("TLS")
                sc.init(null, trustAllCerts, SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }

        fun getTransactionAmount(qrCodeContents: String) : Int
        {
            return (parseQrCodeContent(qrCodeContents)["54"] as String).toInt()
        }

        private fun parseQrCodeContent(qrCodeContents: String) : MutableMap<String, Any>
        {
            var qrCodeContentsTemp = qrCodeContents
            val newMap= mutableMapOf<String, Any>()

            while (qrCodeContentsTemp.isNotBlank())
            {
                val tag = qrCodeContentsTemp.substring(0..1)
                val length = qrCodeContentsTemp.substring(2..3).toInt()
                val content = qrCodeContentsTemp.substring(4..(3+length))

                newMap.put(tag, content as Any)

                qrCodeContentsTemp = qrCodeContentsTemp.substring(4+length)
            }
            return newMap
        }

        fun sendPaymentRequest(qrContent: String, transactionAmount: Int) : Boolean
        {
            val myObject =  object {
                val returnCode = 1000
                val returnDesc = "success"
                val receiptMsgCustomer = "beko Campaign/n2018"
                val receiptMsgMerchant = "beko Campaign Merchant/n2018"
                val paymentInfoList = arrayOf(
                    object {
                        val paymentProcessorID = 67
                        val paymentActionList = arrayOf(
                            object {
                                val paymentType = 3
                                val amount = transactionAmount
                                val currencyID = 949
                                val vatRate = 800
                            }
                        )
                    }
                )
                val QRdata = qrContent
            }

            return try {
                checkReturnCode(sendRequest("https://sandbox-api.payosy.com/api/payment", Klaxon().toJsonString(myObject)))
            } catch (e: Exception) {
                false
            }
        }

        fun sendQRRequest(amount: Int): String {
            val myObject =  object {
                val totalReceiptAmount = amount
            }

            val returnedRequest = sendRequest("https://sandbox-api.payosy.com/api/get_qr_sale", Klaxon().toJsonString(myObject))

            if (!checkReturnCode(returnedRequest))
                throw Exception("FAIL")

            return returnedRequest
        }

        private fun checkReturnCode(content: String) : Boolean
        {
            return (Parser.default().parse(content.reader()) as JsonObject).int("returnCode") == 1000
        }

        private fun sendRequest(URL: String, message: String): String {
            ignoreSSL()

            var output: String? = null

            val t = Thread {
                val serverURL = URL
                val url = URL(serverURL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.connectTimeout = 300000
                connection.doOutput = true

                val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)

                connection.setRequestProperty("Content-length", postData.size.toString())
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("x-ibm-client-id", "")
                connection.setRequestProperty("x-ibm-client-secret", "")
                connection.setRequestProperty("Accept", "*/*")

                try {
                    val outputStream = DataOutputStream(connection.outputStream)
                    outputStream.write(postData)
                    outputStream.flush()
                } catch (exception: Exception) {
                    println("There was error while connecting the chat ${exception.message}")
                }


                val inputStream = DataInputStream(connection.inputStream)
                val reader = BufferedReader(InputStreamReader(inputStream))
                output = reader.readLine()

                println(output)
            }
            t.start()
            t.join()

            return output!!
        }

        fun convertAmountToTL(transactionAmount: Int) : String
        {
            val theStringBuilder = StringBuilder(transactionAmount.toString())
            return theStringBuilder.insert(theStringBuilder.length-2, ".").append(" TL").toString()
        }
    }
}