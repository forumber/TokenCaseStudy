package com.forumber.tokencasestudy

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class Database {
    companion object {
        fun addPayment(transactionAmount: Int)
        {
            val payment = hashMapOf(
                "dateTime" to Calendar.getInstance().time,
                "transactionAmount" to transactionAmount
            )

            Firebase.firestore.collection("users")
                .whereEqualTo("email", FirebaseAuth.getInstance().currentUser!!.email)
                .get()
                .addOnSuccessListener { documents ->
                    documents.first().reference.collection("payments").add(payment)
                        .addOnSuccessListener { documentReference ->
                            Log.d("FIRESTORE", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FIRESTORE", "Error adding document", e)
                        }
                }
        }

        fun registerUser(email: String)
        {
            val user = hashMapOf(
                "email" to email
            )

            Firebase.firestore.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("FIRESTORE", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("FIRESTORE", "Error adding document", e)
                }

        }

    }

}