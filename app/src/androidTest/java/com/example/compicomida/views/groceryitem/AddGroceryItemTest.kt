package com.example.compicomida.views.groceryitem


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.compicomida.R
import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.views.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AddGroceryItemTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun clearAppData() {
        LocalDatabase.resetDB()
    }

    @Test
    fun addGroceryItemTest() {
        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.shoppingListsFragment), withContentDescription("Listas"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_navigation_view),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val floatingActionButton = onView(
            allOf(
                withId(R.id.fabNewlist), withContentDescription("Añadir Producto"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragmentContainerView),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.et_list_name),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.et_list_name_layout),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("Prueba"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.btn_add_list), withText("Añadir Lista"),
                childAtPosition(
                    allOf(
                        withId(R.id.addGroceryItemList),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val recyclerView = onView(
            allOf(
                withId(R.id.recyclerGroceryList),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    0
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.fabNewItem), withContentDescription("Añadir Producto"),
                childAtPosition(
                    allOf(
                        withId(R.id.groceryItemsList),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton2.perform(click())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.et_list_name),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.et_list_name_layout),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText2.perform(scrollTo(), replaceText("Patata"), closeSoftKeyboard())

        val materialAutoCompleteTextView = onView(
            allOf(
                withId(R.id.spinner_product_type),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.spinner_product_type_layout),
                        0
                    ),
                    0
                )
            )
        )
        materialAutoCompleteTextView.perform(scrollTo(), click())

        val appCompatCheckedTextView = onData(anything())
            .inRoot(
                isPlatformPopup()
            )
            .atPosition(1)
        appCompatCheckedTextView.perform(click())

        val materialAutoCompleteTextView2 = onView(
            allOf(
                withId(R.id.spinner_product_units),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.spinner_product_units_layout),
                        0
                    ),
                    0
                )
            )
        )
        materialAutoCompleteTextView2.perform(scrollTo(), click())

        val appCompatCheckedTextView2 = onData(anything())
            .inRoot(
                isPlatformPopup()
            )
            .atPosition(1)
        appCompatCheckedTextView2.perform(click())

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.et_product_quantity),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.et_product_quantity_layout),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText3.perform(scrollTo(), replaceText("6"), closeSoftKeyboard())

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.et_product_price),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.et_product_price_layout),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText4.perform(scrollTo(), replaceText("4"), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.btn_add_grocery_item), withText("Añadir Producto"),
                childAtPosition(
                    allOf(
                        withId(R.id.addGroceryItem),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    15
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        val textView = onView(
            allOf(
                withId(R.id.recycler_grocery_item_title), withText("Patata"),
                withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Patata")))

        val textView2 = onView(
            allOf(
                withId(R.id.recycler_grocery_item_text), withText("Cantidad: 6 kg"),
                withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Cantidad: 6 kg")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
