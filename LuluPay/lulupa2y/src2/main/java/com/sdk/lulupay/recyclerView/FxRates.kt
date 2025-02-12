package com.sdk.lulupay.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdk.lulupay.R

data class FxRates(
    val costRate: String,
    val rate: String,
    val baseCurrencyCode: String,
    val counterCurrencyCode: String,
    val type: String
)

class FxRatesAdapter(
    private val fxRates: List<FxRates>,
    private val onItemClick: (position: Int, fxRates: FxRates) -> Unit
) : RecyclerView.Adapter<FxRatesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val costRate: TextView = itemView.findViewById(R.id.cost_rate_value)
        val rate: TextView = itemView.findViewById(R.id.rate_value)
        val baseCurrencyCode: TextView = itemView.findViewById(R.id.base_currency_code_value)
        val counterCurrencyCode: TextView = itemView.findViewById(R.id.counter_currency_code_value)
        val type: TextView = itemView.findViewById(R.id.type_value)
        val costRateContainer: LinearLayout = itemView.findViewById(R.id.cost_rate_container)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position, fxRates[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fx_rate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = fxRates[position]

        // Set costRate visibility
        if (!item.costRate.isNullOrEmpty()) {
            holder.costRate.text = item.costRate ?: ""
            holder.costRateContainer.visibility = View.VISIBLE
        } else {
            holder.costRateContainer.visibility = View.GONE
        }
        
        
        holder.rate.text = item.rate
        holder.baseCurrencyCode.text = item.baseCurrencyCode
        holder.counterCurrencyCode.text = item.counterCurrencyCode
        holder.type.text = item.type
    }

    override fun getItemCount(): Int {
        return fxRates.size
    }
}