package com.sdk.lulupay.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdk.lulupay.R

data class CorrespondentRules(
    val field: String,
    val rule: String,
)

class CorrespondentRulesAdapter(
    private val correspondentRules: List<CorrespondentRules>,
    private val onItemClick: (position: Int, correspondentRules: CorrespondentRules) -> Unit
) : RecyclerView.Adapter<CorrespondentRulesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val field: TextView = itemView.findViewById(R.id.field_value)
        val rule: TextView = itemView.findViewById(R.id.rules_value)
        val fullContainer: LinearLayout = itemView.findViewById(R.id.correspondent_rules_container)
        val fieldContainer: LinearLayout = itemView.findViewById(R.id.field_container)
        val ruleContainer: LinearLayout = itemView.findViewById(R.id.rules_container)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position, correspondentRules[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.correspondent_rules, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = correspondentRules[position]
        
        if(item.field.isNullOrEmpty() && item.rule.isNullOrEmpty()){
        holder.fullContainer.visibility = View.GONE
        }
        
        if(!item.field.isNullOrEmpty()){
        holder.fullContainer.visibility = View.VISIBLE
        holder.fieldContainer.visibility = View.VISIBLE
        holder.field.text = item.field
        }
        
        if(!item.rule.isNullOrEmpty()){
        holder.fullContainer.visibility = View.VISIBLE
        holder.ruleContainer.visibility = View.VISIBLE
        holder.rule.text = item.rule
        }
        
    }

    override fun getItemCount(): Int {
        return correspondentRules.size
    }
}