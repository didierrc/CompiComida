package com.example.compicomida.views.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.views.fragments.homeTabs.TabExpireItems
import com.example.compicomida.views.fragments.homeTabs.TabSuggestedRecipe
import com.example.compicomida.views.fragments.homeTabs.TabSuperNearYou

class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = CompiComidaApp.HOME_NUMBER_TABS // Number of tabs

    // Switch fragments depending on the tab number
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TabExpireItems()
            1 -> TabSuggestedRecipe()
            2 -> TabSuperNearYou()
            else -> TabExpireItems()
        }
    }
}