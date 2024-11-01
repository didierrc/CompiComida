package com.example.compicomida.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.compicomida.R

/**
 * AddGroceryListFragment:
 * - Shows a form to create a new grocery list.
 */
class AddGroceryListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_grocery_list, container, false)
    }

}