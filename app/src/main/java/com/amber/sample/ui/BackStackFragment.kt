package com.amber.sample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amber.sample.R
import com.amber.sample.databinding.FragmentBackStackBinding

class BackStackFragment : Fragment() {

    lateinit var binding: FragmentBackStackBinding
    var str = ""

    companion object {
        val KEY_STR = "key_str"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            str = it.getString(KEY_STR).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBackStackBinding.inflate(inflater, container, false).apply {
            tv.text = str
        }
        return binding.root
    }
}