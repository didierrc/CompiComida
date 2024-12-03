package com.example.compicomida.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.viewmodels.factories.RecipesViewModelFactory
import com.example.compicomida.viewmodels.recipe.RecipesViewModel
import com.example.compicomida.views.activities.recipe.RecipeDetailsActivity
import com.example.compicomida.views.adapters.RecipesListAdapter

/**
 * Recipes Fragment:
 * - Shows a list from Firebase with all the recipes available.
 */
class RecipesFragment : Fragment() {

    // View Model
    private lateinit var recipesModel: RecipesViewModel

    // Recycler Lists
    private lateinit var recyclerRecipesList: RecyclerView

    // Adapter Lists
    private lateinit var recipesAdapter: RecipesListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_recipes, container, false)

        // At the beginning, recipe recycler show an empty list.
        recyclerRecipesList = root.findViewById(R.id.recyclerRecipesList)
        recyclerRecipesList.layoutManager = LinearLayoutManager(requireContext())

        // Initialise the view model
        recipesModel = ViewModelProvider(
            this,
            RecipesViewModelFactory(CompiComidaApp.appModule.recipesRepo)
        )[RecipesViewModel::class.java]

        // Initialise the recycler views.
        initialise(root)

        return root
    }

    /**
     * - Initialise the recycler views to empty lists.
     * - Observe the recipe list in the view model.
     */
    private fun initialise(root: View) {

        // Initialise the recycler views to empty lists.
        recipesAdapter = RecipesListAdapter(listOf()) { recipeId ->
            recipeId?.let {
                val intent = Intent(root.context, RecipeDetailsActivity::class.java)
                intent.putExtra("recipeId", it)
                startActivity(intent)
            }
        }
        recyclerRecipesList.adapter = recipesAdapter

        // Observe recipes from Firestore
        recipesModel.recipesList.observe(viewLifecycleOwner) { recipesList ->
            recipesAdapter.updateList(recipesList)
        }

        // Fetching data
        recipesModel.refreshRecipesList()
    }


}