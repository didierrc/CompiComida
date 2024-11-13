package com.example.compicomida

import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.example.compicomida.databinding.ActivityRecipeDetailsBinding
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.GroceryList
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
        onBackPressed()
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

                with(binding) {

                    toolbarLayout.title = it.get("name").toString()
                    fondoRecipe.load(it.get("imageUrl").toString())

                    with(contentRecipes) {
                        tvOther?.text =
                            getString(
                                R.string.recipe_other_information,
                                it.get("category"),
                                it.get("preparation_time"),
                                it.get("diner")
                            )
                        difficultyBar?.rating = it.get("difficulty").toString().toFloat()
                        descriptionRecipeText?.text = it.get("description").toString()

                        val ingredients = it.get("ingredients") as List<*>
                        ingredients.forEach {
                            if (ingredientsLayout != null) {
                                addIngredientView(ingredientsLayout, it as HashMap<*, *>)
                            }
                        }

                        val steps = it.get("steps") as List<*>
                        steps.forEachIndexed { index, step ->
                            if (stepsUpperLayout != null) {
                                addStepsView(stepsUpperLayout, step.toString(), index + 1)
                            }
                        }
                    }
                }
            }
    }

    private fun initialiseFab(db: LocalDatabase) {

        binding.fabAddRecipeToList.setOnClickListener {

            addToShoppingList(db)

            Toast.makeText(
                this,
                "Receta añadida a la lista de la compra",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun addToShoppingList(dbLocal: LocalDatabase) {

        db.collection(RECIPES_COLLECTION).document(recipeId.toString()).get()
            .addOnSuccessListener { recipe ->

                val recipeName = recipe["name"].toString()

                // TODO: Build ingredients list from recipe

                lifecycleScope.launch(Dispatchers.IO) {
                    var groceryList: GroceryList? = GroceryList(0, recipeName, LocalDateTime.now())
                    groceryList?.let { dbLocal.groceryListDao().add(it) }

                    // Obtener la lista de la compra guardad en BD.
                    groceryList = dbLocal.groceryListDao().getLastInserted()

                    //TODO: Insert ingredients to grocery list
                }


            }
    }

    private fun addIngredientView(container: LinearLayout, ingredient: HashMap<*, *>) {

        val ingredientIcon = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
            contentDescription = "Icono para el ingrediente: ${ingredient["name"]}"
            setImageResource(R.drawable.shopping_basket_24px)
        }

        val ingredientText = TextView(this).apply {
            id = View.generateViewId()
            text = if (ingredient["quantity"] == null && ingredient["unit"] == null)
                ingredient["name"].toString()
            else
                context?.getString(
                    R.string.recipe_ingredient_detail, ingredient["name"],
                    ingredient["quantity"] ?: "",
                    ingredient["unit"] ?: ""
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
                20.dp,
                20.dp
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

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()