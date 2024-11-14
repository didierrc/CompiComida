package com.example.compicomida.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.RecipeDetailsActivity
import com.example.compicomida.db.entities.recipes.Recipe
import com.example.compicomida.recyclerViews.RecipesListAdapter
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Recipes Fragment:
 * - Shows a list from Firebase with all the recipes available.
 */
class RecipesFragment : Fragment() {

    private lateinit var recyclerRecipesList: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val RECIPES_COLLECTION = "recipes"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Shows all the current recipes from Firebase
        initialiseRecyclerRecipesList()
    }

    private fun initialiseRecyclerRecipesList() {
        recyclerRecipesList = requireView().findViewById(R.id.recyclerRecipesList)
        recyclerRecipesList.layoutManager = LinearLayoutManager(requireContext())

        db.collection(RECIPES_COLLECTION).get()
            .addOnSuccessListener { result ->

                val recipesList = result.documents.mapNotNull {
                    it.toObject(Recipe::class.java)?.copy(id = it.id)
                }

                recyclerRecipesList.adapter = RecipesListAdapter(recipesList) { recipeId ->
                    recipeId?.let {
                        val intent = Intent(requireContext(), RecipeDetailsActivity::class.java)
                        intent.putExtra("recipeId", it)
                        startActivity(intent)
                    }
                }

            }

    }


}