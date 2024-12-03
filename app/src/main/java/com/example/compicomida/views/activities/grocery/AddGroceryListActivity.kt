package com.example.compicomida.views.activities.grocery

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityAddGroceryItemListBinding
import com.example.compicomida.viewmodels.factories.AddGroceryListViewModelFactory
import com.example.compicomida.viewmodels.grocery.AddGroceryListViewModel

class AddGroceryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddGroceryItemListBinding
    private lateinit var viewModel: AddGroceryListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddGroceryItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addGroceryItemList)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        // Initialise the view model
        viewModel = ViewModelProvider(
            this,
            AddGroceryListViewModelFactory(
                CompiComidaApp.appModule.groceryRepo
            )
        )[AddGroceryListViewModel::class.java]
        setUpActionBar()
        addOnClickListener()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }


    private fun addOnClickListener() {
        binding
            .btnAddList.setOnClickListener {
                val listName = binding.etListName.text.toString().trim()
                viewModel.addGroceryList(listName, {
                    binding.etListName.text?.clear()
                    setResult(Activity.RESULT_OK)
                    finish()
                }, { errorMessage ->
                    CompiComidaApp.appModule.showAlert(this, errorMessage)
                })
            }
    }

}