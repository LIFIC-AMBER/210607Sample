package com.amber.sample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.amber.sample.R
import com.amber.sample.databinding.FragmentMotionBinding
import kotlinx.coroutines.flow.combine

class MotionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMotionBinding.inflate(inflater, container, false).apply {
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.backStackFragment2, bundleOf(BackStackFragment.KEY_STR to "Motion Next!!"))
            }
        }

        return binding.root
    }
}