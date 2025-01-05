package com.example.compicomida.views.fragments.homeTabs

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil3.load
import com.example.compicomida.CompiComidaApp.Companion.appModule
import com.example.compicomida.R
import com.example.compicomida.databinding.FragmentTabSuggestedRecipeBinding
import com.example.compicomida.dp
import com.example.compicomida.model.recipeEntities.Ingredient
import com.example.compicomida.model.recipeEntities.Recipe
import com.example.compicomida.viewmodels.home.SuggestedRecipeTabViewModel
import com.example.compicomida.viewmodels.home.factory.SuggestedRecipeTabViewModelFactory
import com.google.android.material.snackbar.Snackbar

class TabSuggestedRecipe : Fragment() {

    // ViewModel
    private lateinit var suggestedModel: SuggestedRecipeTabViewModel

    // Binding
    private var _binding: FragmentTabSuggestedRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Activating binding
        _binding = FragmentTabSuggestedRecipeBinding.inflate(inflater, container, false)

        // Initialise the view model
        suggestedModel = ViewModelProvider(
            this,
            SuggestedRecipeTabViewModelFactory(
                appModule.recipesRepo,
                appModule.groceryRepo,
                appModule.preferencesDailyRecipe
            )
        )[SuggestedRecipeTabViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseFab()

        suggestedModel.recipe.observe(viewLifecycleOwner) { recipe ->
            makeVisibleAllElements()
            updateUI(recipe)
        }
    }

    private fun makeVisibleAllElements() {
        with(binding) {
            suggestedRecipeImage.visibility = View.VISIBLE
            suggestedTvTitle.visibility = View.VISIBLE
            suggestedRecipeMainDescriptionLayout.visibility = View.VISIBLE
            divider2.visibility = View.VISIBLE
            suggestedDescriptionLayout.visibility = View.VISIBLE
            divider4.visibility = View.VISIBLE
            suggestedIngredientsUpperLayout.visibility = View.VISIBLE
            divider6.visibility = View.VISIBLE
            suggestedStepsUpperLayout.visibility = View.VISIBLE
            suggestedRecipeFab.visibility = View.VISIBLE
            progressBarRecipes.visibility = View.GONE
        }
    }


    private fun initialiseFab() {
        with(binding) {
            suggestedRecipeFab.setOnClickListener {

                suggestedModel.addRecipeToGroceryList(context)

                // Showing notification of success
                Snackbar.make(
                    root,
                    getString(R.string.recipe_details_add_to_shopping_list_success),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateUI(recipe: Recipe) {

        with(binding) {

            suggestedRecipeImage.load(recipe.imageUrl)
            suggestedTvTitle.text = recipe.name

            suggestedTvOther.text =
                getString(
                    R.string.recipe_other_information,
                    recipe.category,
                    recipe.preparationTime,
                    recipe.diner
                )
            suggestedDifficultyBar.rating = recipe.difficulty.toFloat()
            suggestedDescriptionRecipeText.text = recipe.description

            recipe.ingredients.forEach { ingredient ->
                addIngredientView(suggestedIngredientsLayout, ingredient)
            }

            recipe.steps.forEachIndexed { index, step ->
                addStepsView(suggestedStepsUpperLayout, step, index + 1)
            }
        }

    }

    private fun addIngredientView(container: LinearLayout, ingredient: Ingredient) {

        val ingredientIcon = ImageView(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
            contentDescription =
                getString(R.string.ingredient_icon_content_description, ingredient.name)
            setImageResource(R.drawable.shopping_basket_24px)
        }

        val ingredientText = TextView(context).apply {
            id = View.generateViewId()
            text = if (ingredient.quantity == null && ingredient.unit == null)
                ingredient.name
            else
                context?.getString(
                    R.string.recipe_ingredient_detail, ingredient.name,
                    if (ingredient.quantity == null) "" else appModule.parseQuantity(
                        ingredient.quantity
                    ),
                    ingredient.unit ?: ""
                )

            setTextAppearance(com.google.android.material.R.style.TextAppearance_AppCompat_Body1)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                weight = 1f
                marginStart = 5.dp
            }
        }

        val ingredientLayout = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(0, 10.dp, 0, 0)
            }
        }

        // Add icon and text to the ingredient layout
        ingredientLayout.addView(ingredientIcon)
        ingredientLayout.addView(ingredientText)

        // Add the ingredient layout to the container
        container.addView(ingredientLayout)
    }

    private fun addStepsView(container: LinearLayout, step: String, index: Int) {

        val stepGuideline = Guideline(context).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                orientation = ConstraintLayout.LayoutParams.VERTICAL
                guideBegin = 40.dp
            }
        }

        val stepNumber = TextView(context).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                30.dp,
                27.dp
            ).apply {
                endToStart = stepGuideline.id
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            background = ContextCompat.getDrawable(context, R.drawable.circle_background)
            gravity = Gravity.CENTER
            text = "$index"
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        val stepText = TextView(context).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            ).apply {
                marginStart = 50.dp
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToEnd = stepGuideline.id
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            text = step
            setTextColor(ContextCompat.getColor(context, R.color.textColor))
            setLineSpacing(2.dp.toFloat(), 1.0f)
        }

        val stepLayout = context?.let {
            ConstraintLayout(it).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 10.dp
                }
            }
        }

        // Add step number and text to the step layout
        stepLayout?.addView(stepNumber)
        stepLayout?.addView(stepGuideline)
        stepLayout?.addView(stepText)

        // Add the step layout to the container
        container.addView(stepLayout)
    }

}