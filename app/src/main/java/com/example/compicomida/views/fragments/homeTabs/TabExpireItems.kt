package com.example.compicomida.views.fragments.homeTabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.databinding.FragmentTabExpireItemsBinding
import com.example.compicomida.viewmodels.home.ExpireTabViewModel
import com.example.compicomida.viewmodels.home.factory.ExpireTabViewModelFactory
import com.example.compicomida.views.adapters.ExpireItemsAdapter

class TabExpireItems : Fragment() {

    // View Model
    private lateinit var expireModel: ExpireTabViewModel

    // Recycler List
    private lateinit var expireRecycler: RecyclerView

    // Adapter Lists
    private lateinit var expireAdapter: ExpireItemsAdapter

    // Binding
    private var _binding: FragmentTabExpireItemsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Activating binding
        _binding = FragmentTabExpireItemsBinding.inflate(inflater, container, false)

        // Initialise the view model
        expireModel = ViewModelProvider(
            this,
            ExpireTabViewModelFactory(CompiComidaApp.appModule.pantryRepo)
        )[ExpireTabViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // At the beginning, recycler show an empty list.
        expireRecycler = binding.homeExpireRecycler
        expireRecycler.layoutManager = LinearLayoutManager(context)
        expireAdapter = ExpireItemsAdapter(listOf())
        expireRecycler.adapter = expireAdapter

        observeExpireItems() // Any change on the expire list will be observed.
        handleChipGroup()
        binding.expireFiltersChipGroup.check(binding.chipExpireItemsAll.id) // Checking ALL by default.
    }

    private fun observeExpireItems() {
        expireModel.expireList.observe(viewLifecycleOwner) { expireList ->
            expireAdapter.updateList(expireList)
        }
    }

    private fun handleChipGroup() {
        binding.expireFiltersChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds[0]) { // Single selection mode!
                binding.chipExpireItemsAll.id -> {
                    expireModel.refreshExpireList()
                    Log.d("Chip", "All")
                }

                binding.chipExpireItemsToday.id -> {
                    expireModel.refreshExpireList(CompiComidaApp.TODAY_FILTER)
                    Log.d("Chip", "Today")
                }

                binding.chipExpireItemsTomorrow.id -> {
                    expireModel.refreshExpireList(CompiComidaApp.TOMORROW_FILTER)
                    Log.d("Chip", "Tomorrow")
                }

                binding.chipExpireItems2days.id -> {
                    expireModel.refreshExpireList(CompiComidaApp.TWO_DAYS_FILTER)
                    Log.d("Chip", "2 Days")
                }

            }
        }
    }
}