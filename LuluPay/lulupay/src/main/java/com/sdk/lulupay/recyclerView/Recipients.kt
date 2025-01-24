package com.sdk.lulupay.recyclerView;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdk.lulupay.R

data class Receipients(
    val firstName: String,
    val lastName: String,
    val phoneNo: String,
    val transactionRefNo: String
)

class ReceipientsAdapter(
    private val receipients: List<Receipients>,
    private val onItemClick: (position: Int, receipients: Receipients) -> Unit
) : RecyclerView.Adapter<ReceipientsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contactName)
        val contactNumber: TextView = itemView.findViewById(R.id.contactNumber)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position, receipients[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = receipients[position]
        
        holder.contactName.text = item.firstName + " " + item.lastName
        holder.contactNumber.text = item.phoneNo
    }

    override fun getItemCount(): Int {
        return receipients.size
    }
}