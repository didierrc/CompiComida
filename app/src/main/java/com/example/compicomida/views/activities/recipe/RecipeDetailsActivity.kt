package com.example.compicomida.views.activities.recipe

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityRecipeDetailsBinding
import com.example.compicomida.dp
import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.GroceryList
import com.example.compicomida.model.recipeEntities.Ingredient
import com.example.compicomida.model.recipeEntities.Recipe
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Recipes Details Activity:
 * - Shows the details of a recipe extract from Firebase.
 */
class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailsBinding
    private var recipeId: Int = -1

    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val RECIPES_COLLECTION = "recipes"
    }

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

        // Id de la receta a mostrar detalles
        recipeId = intent.getIntExtra("recipeId", -1)
        if (recipeId != -1) initialiseView(recipeId.toString())

        // Inicializando base de datos local
        val dbLocal = LocalDatabase.getDB(this)
        initialiseFab(dbLocal)
    }

    private fun initialiseView(recipeId: String) {

        db.collection(RECIPES_COLLECTION).document(recipeId).get()
            .addOnSuccessListener {
                if (it != null && it.exists()) {
                    val recipe = it.toObject(Recipe::class.java)?.copy(id = it.id)
                    recipeCallback(recipe)
                } else {
                    Log.d("Recipe Details FAIL", "No such document")
                    recipeCallback(null)
                }
            }
    }

    private fun recipeCallback(recipe: Recipe?) {
        recipe?.let {

            with(binding) {

                toolbarLayout.title = recipe.name
                fondoRecipe.load(recipe.imageUrl)

                with(contentRecipes) {
                    tvOther?.text =
                        getString(
                            R.string.recipe_other_information,
                            it.category,
                            it.preparationTime,
                            it.diner
                        )
                    difficultyBar?.rating = it.difficulty.toFloat()
                    descriptionRecipeText?.text = it.description

                    it.ingredients.forEach { ingredient ->
                        if (ingredientsLayout != null) {
                            addIngredientView(ingredientsLayout, ingredient)
                        }
                    }

                    it.steps.forEachIndexed { index, step ->
                        if (stepsUpperLayout != null) {
                            addStepsView(stepsUpperLayout, step, index + 1)
                        }
                    }
                }
            }


        }
    }

    private fun initialiseFab(db: LocalDatabase) {

        binding.fabAddRecipeToList.setOnClickListener {

            addToShoppingList(db)

            Snackbar.make(
                binding.root,
                "Receta añadida a la lista de la compra",
                Snackbar.LENGTH_SHORT
            ).show()

            // Way to go directly to Lists? finish()
        }
    }

    private fun addToShoppingList(dbLocal: LocalDatabase) {

        db.collection(RECIPES_COLLECTION).document(recipeId.toString()).get()
            .addOnSuccessListener {

                if (it != null && it.exists()) {
                    val recipe = it.toObject(Recipe::class.java)?.copy(id = it.id)
                    addToShopListCallback(dbLocal, recipe)
                } else {
                    Log.d("Recipe Details FAIL", "No such document")
                    addToShopListCallback(dbLocal, null)
                }

            }
    }

    private fun addToShopListCallback(dbLocal: LocalDatabase, recipe: Recipe?) {
        recipe?.let {
            lifecycleScope.launch(Dispatchers.IO) {

                // Creating and Storing the Grocery List from the Recipe.
                var groceryList: GroceryList? =
                    GroceryList(0, recipe.name, LocalDateTime.now())
                groceryList?.let { dbLocal.groceryListDao.add(it) }
                groceryList = dbLocal.groceryListDao.getLastInserted()

                // Inserting ingredients from Recipe to the Grocery List.
                val ingredientsList = mutableListOf<GroceryItem>()
                groceryList?.let {

                    recipe.ingredients.forEach { recipeIngredient ->

                        ingredientsList.add(
                            GroceryItem(
                                itemId = 0,
                                listId = groceryList.listId,
                                categoryId = null,
                                itemName = recipeIngredient.name,
                                quantity = recipeIngredient.quantity ?: 0.0,
                                unit = recipeIngredient.unit ?: "No especificada",
                                price = 0.0,
                                isPurchased = false,
                                itemPhotoUri = "https://cdn-icons-png.flaticon.com/512/1261/1261163.png"
                            )
                        )
                    }
                }

                dbLocal.groceryItemDao.addAll(*ingredientsList.toTypedArray())
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
                    parseQuantity(ingredient.quantity),
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

    private fun parseQuantity(quantity: Double?): String {

        if (quantity == null)
            return ""

        val parse = if (quantity.mod(1.0) == 0.0) {
            quantity.toInt().toString()
        } else {
            if (quantity == 0.5)
                "1/2"
            else
                quantity.toString()
        }

        return parse
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
            setTextColor(ContextCompat.getColor(context, R.color.black))
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