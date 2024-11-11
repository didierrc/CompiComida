package com.example.compicomida.fragment

import android.content.res.Resources
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
import androidx.navigation.fragment.navArgs
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.databinding.FragmentRecipesDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Recipes Details Fragment:
 * - Shows the details of a recipe extracted from Firebase.
 */
class RecipesDetailsFragment : Fragment() {

    private var _binding: FragmentRecipesDetailsBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val args: RecipesDetailsFragmentArgs by navArgs()

    companion object {
        const val RECIPES_COLLECTION = "recipes"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
    }

    private fun initialiseView() {
        db.collection(RECIPES_COLLECTION)
            .document(args.recipeId.toString())
            .get().addOnSuccessListener {

                with(binding) {
                    toolbarLayout.title = it.get("name").toString()
                    fondoRecipe.load(it.get("imageUrl").toString())

                    with(contentRecipes) {
                        tvOther.text =
                            context?.getString(
                                R.string.recipe_other_information,
                                it.get("category"),
                                it.get("preparation_time"),
                                it.get("diner")
                            )
                        difficultyBar.rating = it.get("difficulty").toString().toFloat()
                        descriptionRecipeText.text = it.get("description").toString()

                        val ingredients = it.get("ingredients") as List<*>
                        ingredients.forEach {
                            addIngredientView(ingredientsLayout, it as HashMap<*, *>)
                        }

                        val steps = it.get("steps") as List<*>
                        steps.forEachIndexed { index, step ->
                            addStepsView(stepsUpperLayout, step.toString(), index + 1)
                        }
                    }
                }
            }

        binding.fabAddRecipeToList.setOnClickListener {
            //TODO: add recipe to list
        }
    }

    private fun addIngredientView(container: LinearLayout, ingredient: HashMap<*, *>) {

        val ingredientIcon = ImageView(context).apply {
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

        val ingredientText = TextView(context).apply {
            id = View.generateViewId()
            if (ingredient["quantity"] == null && ingredient["unit"] == null)
                text = ingredient["name"].toString()
            else
                text = context?.getString(
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
            setTextColor(ContextCompat.getColor(context, R.color.black))
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

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()