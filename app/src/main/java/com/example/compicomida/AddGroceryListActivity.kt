package com.example.compicomida

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.compicomida.databinding.ActivityAddGroceryItemListBinding
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.GroceryList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class AddGroceryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddGroceryItemListBinding
    private lateinit var db: LocalDatabase
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

        // Init db
        db = LocalDatabase.getDB(this)
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
                if (listName.isBlank()) {
                    val message = "El nombre de la lista no puede estar vacío"
                    showAlert(message)
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (db.groceryListDao().getByName(listName) != null) {
                            showAlert("La lista ya existe")
                        } else {
                            db.groceryListDao()
                                .add(GroceryList(0, listName, LocalDateTime.now()))
                            binding.etListName.text?.clear()
                            Log.e(
                                "AddGroceryListFragment",
                                "List added: $listName"
                            )
                            withContext(Dispatchers.Main) {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                }
            }
    }

    private fun showAlert(message: String, title: String = "Error") {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}