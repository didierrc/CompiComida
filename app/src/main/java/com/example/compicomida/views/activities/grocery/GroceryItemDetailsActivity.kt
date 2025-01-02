package com.example.compicomida.views.activities.grocery

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityGroceryItemDetailsBinding
import com.example.compicomida.viewmodels.grocery.GroceryItemDetailsViewModel
import com.example.compicomida.viewmodels.grocery.factory.GroceryItemDetailsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroceryItemDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroceryItemDetailsBinding
    private var groceryItemID: Int = 0
    private lateinit var groceryItemDetailsViewModel: GroceryItemDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroceryItemDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root) // Use binding.root here
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.grocery_item_details)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        groceryItemID = intent.getIntExtra(GroceryItemsListActivity.ID_TAG, 0)

        groceryItemDetailsViewModel = ViewModelProvider(
            this,
            GroceryItemDetailsViewModelFactory(
                CompiComidaApp.appModule.groceryRepo
            )
        )[GroceryItemDetailsViewModel::class.java]

        configureBehaviour()


    }

    private fun configureBehaviour() {
        setUpActionBar()
        groceryItemDetailsViewModel.refreshGroceryItemDetails(groceryItemID, this)

        groceryItemDetailsViewModel.groceryItemUIState.observe(this) { groceryItemDetailsUI ->
            with(binding) {
                itemName.text = groceryItemDetailsUI.itemNameTxt
                itemCategory.text = groceryItemDetailsUI.itemCategory
                itemQuantity.text = groceryItemDetailsUI.unitsTxt
                itemPrice.text = groceryItemDetailsUI.priceTxt
                itemCheckbox.isChecked = groceryItemDetailsUI.checkState
                itemImage.load(groceryItemDetailsUI.imageURI)
                toolbar.title =
                    groceryItemDetailsUI.itemNameTxt + getString(R.string.grocery_item_details_leading_title)
            }
        }

        with(binding) {
            itemCheckbox.setOnClickListener {
                groceryItemDetailsViewModel.checkItem(
                    binding.itemCheckbox.isChecked,
                    groceryItemID
                )
            }
            btnDelete.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    groceryItemDetailsViewModel.removeGroceryItem(groceryItemID)
                    finish()
                }
            }
        }


    }

    private fun setUpActionBar() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}