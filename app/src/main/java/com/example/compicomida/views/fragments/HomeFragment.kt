package com.example.compicomida.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.viewmodels.factories.HomeViewModelFactory
import com.example.compicomida.viewmodels.home.HomeViewModel
import com.example.compicomida.views.adapters.ExpireItemsAdapter
import com.example.compicomida.views.adapters.RecentListItemsAdapter

/**
 * Home Fragment:
 * - Shows the products near to expire from your pantry.
 * - Shows the products from the most recent shop list.
 */
class HomeFragment : Fragment() {

    // View Model
    private lateinit var homeModel: HomeViewModel

    // Recycler Lists
    private lateinit var expireRecycler: RecyclerView
    private lateinit var recentListRecycler: RecyclerView

    // Adapter Lists
    private lateinit var expireAdapter: ExpireItemsAdapter
    private lateinit var recentListAdapter: RecentListItemsAdapter

    /**
     * - Inflate the layout for this fragment.
     * - Initialise the view model.
     * - Initialise the recycler views.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // At the beginning, both recyclers show an empty list.
        expireRecycler = root.findViewById(R.id.homeExpireRecycler)
        expireRecycler.layoutManager = LinearLayoutManager(context)
        recentListRecycler = root.findViewById(R.id.homeRecentListRecycler)
        recentListRecycler.layoutManager = LinearLayoutManager(context)

        // Initialise the view model
        homeModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                CompiComidaApp.appModule.pantryRepo,
                CompiComidaApp.appModule.groceryRepo
            )
        )[HomeViewModel::class.java]

        // Initialise the recycler views.
        initialise(root)

        return root
    }

    /**
     * - Initialise the recycler views to empty lists.
     * - Observe the expire list and the recent list in the view model.
     */
    private fun initialise(root: View) {
        // Initialise the recycler views to empty lists.
        expireAdapter = ExpireItemsAdapter(listOf())
        expireRecycler.adapter = expireAdapter
        recentListAdapter = RecentListItemsAdapter(listOf())
        recentListRecycler.adapter = recentListAdapter

        // Observe expire data and recent list data in ViewModel.
        homeModel.expireList.observe(viewLifecycleOwner) { expireList ->
            expireAdapter.updateList(expireList)
        }

        homeModel.recentList.observe(viewLifecycleOwner) { recentList ->

            // Updating the most recent list title if new
            val title = root.findViewById<TextView>(R.id.homeRecentListTitle)
            title.text = String.format(
                getString(R.string.tv_home_recent_list_title),
                homeModel.recentListName.value
            )

            recentListAdapter.updateList(recentList)
        }

        // Fetching the data.
        refreshData()
    }

    private fun refreshData() {
        homeModel.refreshExpireList()
        homeModel.refreshRecentList()
    }

}