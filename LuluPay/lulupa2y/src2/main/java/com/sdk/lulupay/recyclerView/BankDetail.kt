package com.sdk.lulupay.recyclerView;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdk.lulupay.R

data class BankDetail(
    val bankName: String,
    val routingCode: String,
    val bicSwift: String,
    val sortCode: String,
    val address: String? = null,
    val townName: String,
    val countrySubdivision: String
)

class BankDetailsAdapter(
    private val bankDetails: List<BankDetail>,
    private val onItemClick: (position: Int, bankDetail: BankDetail) -> Unit
) : RecyclerView.Adapter<BankDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankName: TextView = itemView.findViewById(R.id.bank_name_value)
        val routingCode: TextView = itemView.findViewById(R.id.routing_code_value)
        val bicSwift: TextView = itemView.findViewById(R.id.bic_swift_value)
        val sortCode: TextView = itemView.findViewById(R.id.sort_code_value)
        val address: TextView = itemView.findViewById(R.id.address_value)
        val townName: TextView = itemView.findViewById(R.id.town_name_value)
        val countrySubdivision: TextView = itemView.findViewById(R.id.country_subdivision_value)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position, bankDetails[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bank_details, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = bankDetails[position]
        holder.bankName.text = item.bankName
        holder.routingCode.text = item.routingCode
        holder.bicSwift.text = item.bicSwift
        holder.sortCode.text = item.sortCode
        holder.address.text = item.address
        holder.townName.text = item.townName
        holder.countrySubdivision.text = item.countrySubdivision
    }

    override fun getItemCount() = bankDetails.size
}