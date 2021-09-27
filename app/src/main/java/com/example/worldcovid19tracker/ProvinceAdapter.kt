package com.example.worldcovid19tracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.worldcovid19tracker.databinding.ProvinceItemLayoutBinding
import java.text.NumberFormat

class ProvinceAdapter(private val list: MutableList<CovidData>) :
    RecyclerView.Adapter<ProvinceAdapter.ProvinceViewHolder>() {
    inner class ProvinceViewHolder(val binding: ProvinceItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvinceViewHolder {
        return ProvinceViewHolder(
            ProvinceItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProvinceViewHolder, position: Int) {
        holder.binding.provinceName.text = list[position].province
        holder.binding.provinceCases.text =
            NumberFormat.getInstance().format(list[position].cumulative_cases)
        holder.binding.provincialFlag.setImageResource(
            when (list[position].province) {
                "Alberta" -> R.drawable.ic_flag_of_alberta
                "BC" -> R.drawable.ic_flag_of_british_columbia
                "Canada" -> R.drawable.ic_flag_of_canada
                "Manitoba" -> R.drawable.ic_flag_of_manitoba
                "NL" -> R.drawable.ic_flag_of_newfoundland_and_labrador
                "NWT" -> R.drawable.ic_flag_of_northwest_territories
                "New Brunswick" -> R.drawable.ic_flag_of_new_brunswick
                "Nova Scotia" -> R.drawable.ic_flag_of_nova_scotia
                "Nunavut" -> R.drawable.ic_flag_of_nunavut
                "Ontario" -> R.drawable.ic_flag_of_ontario
                "PEI" -> R.drawable.ic_canada_prince_edward_island
                "Quebec" -> R.drawable.ic_flag_of_qu_bec
                "Saskatchewan" -> R.drawable.ic_flag_of_saskatchewan
                "Yukon" -> R.drawable.ic_flag_of_yukon
                else -> R.drawable.ic_flag_of_canada
            }
        )

        holder.binding.root.setOnClickListener {
            val intent = Intent(it.context, MainActivity::class.java)
            intent.putExtra("province", list[position].province)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}