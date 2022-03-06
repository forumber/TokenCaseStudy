package com.forumber.tokencasestudy

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {
    data class Transaction(var dateTime: Date = Calendar.getInstance().time, var transactionAmount: Int = 0)

    class DataAdapter(private val itemList : ArrayList<Transaction>) : RecyclerView.Adapter<DataAdapter.DataHolder>() {
        class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val theTime : TextView = itemView.findViewById(R.id.textDateTime)
            val theTransactionAmount : TextView = itemView.findViewById(R.id.textTransactionAmount)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_item,parent,false)

            return DataHolder(itemView)
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {
            val transaction : Transaction = itemList[position]
            holder.theTime.text = transaction.dateTime.toString()
            holder.theTransactionAmount.text = QSYAPI.convertAmountToTL(transaction.transactionAmount)

        }

        override fun getItemCount(): Int {
            return itemList.size
        }


    }


    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionList : ArrayList<Transaction>
    private lateinit var theAdapter : DataAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recyclerViewHistoryList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        transactionList = arrayListOf()

        theAdapter = DataAdapter(transactionList)

        recyclerView.adapter = theAdapter

        EventChangeListener()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        Firebase.firestore.collection("users")
            .whereEqualTo("email", FirebaseAuth.getInstance().currentUser!!.email)
            .get()
            .addOnSuccessListener { documents ->
                documents.first().reference.collection("payments")
                    .orderBy("dateTime", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                    for (dc : DocumentChange in value!!.documentChanges)
                    {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            transactionList.add(dc.document.toObject(Transaction::class.java))

                        }
                    }

                    theAdapter.notifyDataSetChanged()
                }
            }

    }
}


