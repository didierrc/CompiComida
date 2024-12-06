package com.example.compicomida.views.activities.recipe

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil3.load
import com.example.compicomida.CompiComidaApp.Companion.appModule
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityRecipeDetailsBinding
import com.example.compicomida.dp
import com.example.compicomida.model.recipeEntities.Ingredient
import com.example.compicomida.model.recipeEntities.Recipe
import com.example.compicomida.viewmodels.recipe.RecipesDetailsViewModel
import com.example.compicomida.viewmodels.recipe.factory.RecipesDetailsViewModelFactory
import com.google.android.material.snackbar.Snackbar

/**
 * Recipes Details Activity:
 * - Shows the details of a recipe extract from Firebase.
 */
class RecipeDetailsActivity : AppCompatActivity() {

    // Binding
    private lateinit var binding: ActivityRecipeDetailsBinding

    // ViewModel
    private lateinit var recipesDetailsModel: RecipesDetailsViewModel

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding de los elementos
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Barra de Herramientas con flecha de retroceso
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        // Initialising the view model
        recipesDetailsModel = ViewModelProvider(
            this,
            RecipesDetailsViewModelFactory(appModule.recipesRepo, appModule.groceryRepo)
        )[RecipesDetailsViewModel::class.java]

        // Initialising view elements
        recipesDetailsModel.getRecipe(intent.getIntExtra("recipeId", -1))
        initialiseFab()
        observeRecipe()
    }

    private fun observeRecipe() {
        recipesDetailsModel.recipe.observe(this) { recipe ->
            // If error while fetching the recipe (either id from parent -1 or non-existing document),
            // finish the activity.
            if (recipe != null)
                updateUI(recipe)
            else
                finish()

        }
    }

    private fun initialiseFab() {
        with(binding) {
            fabAddRecipeToList.setOnClickListener {

                // Adding Recipe to a GroceryList
                if (recipesDetailsModel.recipe.value != null) {

                    recipesDetailsModel.addRecipeToGroceryList()

                    // Showing notification of success
                    Snackbar.make(
                        root,
                        getString(R.string.recipe_details_add_to_shopping_list_success),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        root,
                        getString(R.string.recipe_details_add_to_shopping_list_fail),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            }
        }

    }

    private fun updateUI(recipe: Recipe) {

        with(binding) {
            toolbarLayout.title = recipe.name
            fondoRecipe.load(recipe.imageUrl)

            with(contentRecipes) {
                tvOther?.text =
                    getString(
                        R.string.recipe_other_information,
                        recipe.category,
                        recipe.preparationTime,
                        recipe.diner
                    )
                difficultyBar?.rating = recipe.difficulty.toFloat()
                descriptionRecipeText?.text = recipe.description

                recipe.ingredients.forEach { ingredient ->
                    if (ingredientsLayout != null) {
                        addIngredientView(ingredientsLayout, ingredient)
                    }
                }

                recipe.steps.forEachIndexed { index, step ->
                    if (stepsUpperLayout != null) {
                        addStepsView(stepsUpperLayout, step, index + 1)
                    }
                }
            }


        }

    }
    
    private fun addIngredientView(container: LinearLayout, ingredient: Ingredient) {

        val ingredientIcon = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
            contentDescription = "Icono para el ingrediente: ${ingredient.name}"
            setImageResource(R.drawable.shopping_basket_24px)
        }

        val ingredientText = TextView(this).apply {
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

        val ingredientLayout = LinearLayout(this).apply {
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

        val stepGuideline = Guideline(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                orientation = ConstraintLayout.LayoutParams.VERTICAL
                guideBegin = 40.dp
            }
        }

        val stepNumber = TextView(this).apply {
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

        val stepText = TextView(this).apply {
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

        val stepLayout = ConstraintLayout(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 10.dp
            }
        }

        // Add step number and text to the step layout
        stepLayout.addView(stepNumber)
        stepLayout.addView(stepGuideline)
        stepLayout.addView(stepText)

        // Add the step layout to the container
        container.addView(stepLayout)
    }

}