package com.example.luxurycatadmin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.luxurycatadmin.R
import com.example.luxurycatadmin.databinding.FragmentCatBinding

class CatFragment : Fragment() {

    private lateinit var binding : FragmentCatBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCatBinding.inflate(layoutInflater)

        binding.floatingActionButton.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_catFragment_to_addCatFragment)
        }
        return binding.root
    }


}