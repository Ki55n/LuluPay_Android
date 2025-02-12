package com.sdk.lulupay.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdk.lulupay.R

data class SettlementDetails(
    val chargeType: String,
    val value: String,
    val currencyCode: String
)

class SettlementDetailsAdapter(
    private val settlementDetails: List<SettlementDetails>,
    private val onItemClick: (position: Int, settlementDetails: SettlementDetails) -> Unit
) : RecyclerView.Adapter<SettlementDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chargeType: TextView = itemView.findViewById(R.id.charge_type_value)
        val value: TextView = itemView.findViewById(R.id.value)
        val currencyCode: TextView = itemView.findViewById(R.id.currency_value)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position, settlementDetails[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.settlement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = settlementDetails[position]

        // Set other fields
        holder.chargeType.text = item.chargeType
        holder.value.text = item.value
        holder.currencyCode.text = item.currencyCode
    }

    override fun getItemCount(): Int {
        return settlementDetails.size
    }
}