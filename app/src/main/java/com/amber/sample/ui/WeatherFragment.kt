package com.amber.sample.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.amber.sample.R
import com.amber.sample.databinding.FragmentWeatherBinding
import com.amber.sample.repository.WeatherRepository
import com.amber.sample.utils.NetworkUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect

class WeatherFragment: Fragment() {
    private lateinit var binding: FragmentWeatherBinding
    private val viewModel: WeatherViewModel by viewModels {
        MainViewModelFactory(
            WeatherRepository(NetworkUtil.weatherService)
        )
    }

    private var uiStateJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeatherBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vieWModel = this@WeatherFragment.viewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshWeather()

        DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).run {
            ResourcesCompat.getDrawable(resources, R.drawable.divider, requireActivity().theme)
                ?.let { setDrawable(it) }
            binding.list.addItemDecoration(this)
        }

        val adapter = WeatherAdapter(mutableListOf(), viewModel)
        binding.list.adapter = adapter

        uiStateJob = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.weatherListLiveData.collect {
                adapter.itemList = it.toMutableList()
            }

            viewModel.refreshEvent.collect {
                it.getContentIfNotHandled()?.let {
                    when (it) {
                        State.Loading -> {
                            binding.swipe.isRefreshing = true
                        }
                        State.Success -> {
                            binding.swipe.isRefreshing = false
                        }
                        is State.Failed -> {
                            Toast.makeText(requireContext(), getString(it.errorMsg), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            viewModel.clickedWeather.collect {
                it.getContentIfNotHandled()?.let {
                    findNavController().navigate(R.id.backStackFragment, bundleOf(BackStackFragment.KEY_STR to it.toString()))
                }
            }
        }

        // or
//        viewModel.weatherListLiveData.asLiveData().observe(viewLifecycleOwner, Observer {
//            adapter.itemList = it.toMutableList()
//        })
//
//        viewModel.refreshEvent.asLiveData().observe(viewLifecycleOwner, Observer {
//            it.getContentIfNotHandled()?.let {
//                when (it) {
//                    State.Loading -> {
//                        binding.swipe.isRefreshing = true
//                    }
//                    State.Success -> {
//                        binding.swipe.isRefreshing = false
//                    }
//                    is State.Failed -> {
//                        Toast.makeText(requireContext(), getString(it.errorMsg), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        })
//
//        viewModel.clickedWeather.asLiveData().observe(viewLifecycleOwner, Observer {
//            it.getContentIfNotHandled()?.let {
//                findNavController().navigate(R.id.backStackFragment, bundleOf(BackStackFragment.KEY_STR to it.toString()))
//            }
//        })
    }

    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }
}