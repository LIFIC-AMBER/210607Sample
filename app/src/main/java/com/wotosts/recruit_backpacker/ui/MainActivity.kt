package com.wotosts.recruit_backpacker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.wotosts.recruit_backpacker.R
import com.wotosts.recruit_backpacker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            vieWModel = viewModel
        }

        DividerItemDecoration(this, DividerItemDecoration.VERTICAL).run {
            ResourcesCompat.getDrawable(resources, R.drawable.divider, theme)?.let { setDrawable(it) }
            binding.list.addItemDecoration(this)
        }

        val adapter =  WeatherAdapter(mutableListOf())
        binding.list.adapter = adapter


        viewModel.weatherLiveData.observe(this, Observer {
            adapter.itemList = it as MutableList<Any>
        })
    }
}