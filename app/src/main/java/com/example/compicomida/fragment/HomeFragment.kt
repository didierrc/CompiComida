package com.example.compicomida.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.recyclerViews.ExpireItemsAdapter
import com.example.compicomida.recyclerViews.RecentListItemsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Home Fragment:
 * - Shows the products near to expire from your pantry.
 * - Shows the products from the most recent shop list.
 */
class HomeFragment : Fragment() {

    private lateinit var db: LocalDatabase
    private lateinit var homeExpireRecycler: RecyclerView
    private lateinit var homeRecentListRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = LocalDatabase.getDB(requireContext())
        initialiseRecyclers()
    }

    private fun initialiseRecyclers() {

        homeExpireRecycler = requireView().findViewById(R.id.homeExpireRecycler)
        homeExpireRecycler.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {

            val groceryItems = db.pantryItemDao().getCloseExpireItems()
            withContext(Dispatchers.Main) {
                homeExpireRecycler.adapter = ExpireItemsAdapter(groceryItems)
            }
        }

        homeRecentListRecycler = requireView().findViewById(R.id.homeRecentListRecycler)
        homeRecentListRecycler.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {

            val groceryList = db.groceryListDao().getMostRecentList()
            groceryList?.let {
                val groceryItems = db.groceryItemDao().getByListId(it.listId)

                withContext(Dispatchers.Main) {

                    val title = requireView().findViewById<TextView>(R.id.homeRecentListTitle)
                    title.text =
                        String.format(getString(R.string.tv_home_recent_list_title), it.listName)


                    homeRecentListRecycler.adapter = RecentListItemsAdapter(groceryItems)
                }
            }

        }

    }

}