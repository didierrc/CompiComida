package com.example.compicomida.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.compicomida.R
import com.example.compicomida.databinding.FragmentHomeImprovedBinding
import com.example.compicomida.views.adapters.HomeViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Home Fragment:
 * - Shows the products near to expire from your pantry.
 * - Shows the Recipe of the Day.
 * - Shows supermarkets near you.
 */
class HomeFragment : Fragment() {

    // Binding
    private var _binding: FragmentHomeImprovedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeImprovedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.homeTabLayout
        val viewPager = binding.homeViewPager

        // Setup the ViewPager with the adapter
        viewPager.adapter = HomeViewPagerAdapter(this)

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.tab_expire_items)
                    tab.setIcon(R.drawable.priority_high_24px)
                }

                1 -> {
                    tab.text = getString(R.string.tab_suggested_recipe)
                    tab.setIcon(R.drawable.skillet_24px)
                }

                2 -> {
                    tab.text = getString(R.string.tab_markets_near)
                    tab.setIcon(R.drawable.store_24px)
                }
            }
        }.attach()


    }

}