package com.sdk.lulupay.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdk.lulupay.R

data class FeeDetails(
    val type: String,
    val model: String,
    val currencyCode: String,
    val amount: String,
    val description: String
)

class FeeDetailsAdapter(
    private val feeDetails: List<FeeDetails>,
    private val onItemClick: (position: Int, feeDetails: FeeDetails) -> Unit
) : RecyclerView.Adapter<FeeDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val type: TextView = itemView.findViewById(R.id.type_value)
        val model: TextView = itemView.findViewById(R.id.model_value)
        val currencyCode: TextView = itemView.findViewById(R.id.currency_code_value)
        val amount: TextView = itemView.findViewById(R.id.amount_value)
        val description: TextView = itemView.findViewById(R.id.description_value)
        val descriptionContainer: LinearLayout = itemView.findViewById(R.id.description_container)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position, feeDetails[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fee_details, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = feeDetails[position]

        // Set costRate visibility
        if (item.description.isNotEmpty()) {
            holder.description.text = item.description
            holder.descriptionContainer.visibility = View.VISIBLE
        } else {
            holder.descriptionContainer.visibility = View.GONE
        }

        // Set other fields
        holder.type.text = item.type
        holder.model.text = item.model
        holder.currencyCode.text = item.currencyCode
        holder.amount.text = item.amount
    }

    override fun getItemCount(): Int {
        return feeDetails.size
    }
}