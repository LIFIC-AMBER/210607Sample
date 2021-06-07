package com.amber.sample.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.amber.sample.databinding.ItemHeaderBinding
import com.amber.sample.databinding.ItemWeatherRowBinding
import com.amber.sample.model.WeatherRow
import java.lang.Exception

object ViewType {
    const val HEADER = 0
    const val WEATHER = 1
}

class WeatherAdapter(list: MutableList<Any>) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder<*>>() {
    var itemList = list
        set(value) {
            field = mutableListOf<Any>().apply{
                if (value.isNotEmpty()) add(Any())
                addAll(value)
            }

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<*> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ViewType.HEADER -> {
                HeaderViewHolder(ItemHeaderBinding.inflate(layoutInflater, parent, false))
            }
            ViewType.WEATHER -> {
                WeatherViewHolder(ItemWeatherRowBinding.inflate(layoutInflater, parent, false))
            }
            else -> {
                throw Exception("Invalid view type.")
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder<*>, position: Int) {
        when (holder) {
            is HeaderViewHolder -> { /* nothing */ }
            is WeatherViewHolder -> holder.onBind(itemList[position] as WeatherRow)
            else -> {
                throw Exception("Invalid view type.")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> ViewType.HEADER
            else -> ViewType.WEATHER
        }
    }

    abstract class ViewHolder<T>(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun onBind(item: T)
    }

    inner class HeaderViewHolder(val binding: ItemHeaderBinding) :
        ViewHolder<String>(binding) {
        override fun onBind(item: String) {
        }
    }

    inner class WeatherViewHolder(val binding: ItemWeatherRowBinding) :
        ViewHolder<WeatherRow>(binding) {
        override fun onBind(item: WeatherRow) {
            binding.weather = item
        }
    }
}