package com.forumber.tokencasestudy

import android.annotation.SuppressLint
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
        fun ignoreSSL()
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

        fun sendRequest(message: String): String? {
            ignoreSSL()

            var output: String? = null

            val t = Thread {
                val serverURL = "https://sandbox-api.payosy.com/api/get_qr_sale"
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

            return output


        }
    }
}