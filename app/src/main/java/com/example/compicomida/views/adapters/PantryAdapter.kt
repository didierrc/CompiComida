package com.example.compicomida.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.compicomida.AppModule
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.model.localDb.entities.PantryItem
import com.example.compicomida.views.adapters.diff.PantryDiffCallback

class PantryAdapter(
    private var pantryItems: List<PantryItem>,
    private val onClickGoToEditItem: (Int?) -> Unit,
) : RecyclerView.Adapter<PantryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_pantry, viewGroup, false)
        return ViewHolder(view, onClickGoToEditItem)
    }

    override fun getItemCount() = pantryItems.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) =
        viewHolder.bind(pantryItems[position])

    fun updateList(newPantryList: List<PantryItem>) {
        val diffCallback = PantryDiffCallback(pantryItems, newPantryList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        pantryItems = newPantryList
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(
        view: View,
        onClickGoToEditItem: (Int?) -> Unit,
    ) : RecyclerView.ViewHolder(view) {

        private val app: AppModule = CompiComidaApp.appModule

        private val tvTitle: TextView = view.findViewById(R.id.recycler_pantry_item_title)
        private val tvText: TextView = view.findViewById(R.id.recycler_pantry_item_text)
        private val tvExpirationDate: TextView =
            view.findViewById(R.id.recycler_pantry_item_expiration_date)
        private val imageView: ImageView = view.findViewById(R.id.recycler_pantry_item_image)
//      private val btnDeleteItem: Button = view.findViewById(R.id.recycler_pantry_item_btn_delete)

        private lateinit var pantryItem: PantryItem

        init {
            view.setOnClickListener {
                onClickGoToEditItem(pantryItem.pantryId)
            }
        }

        fun bind(pantryItem: PantryItem) {
            this.pantryItem = pantryItem
            tvTitle.text = pantryItem.pantryName
            tvText.text = app.parseUnitQuantity(pantryItem.unit, pantryItem.quantity)
            tvExpirationDate.text = app.parseExpirationDate(pantryItem.expirationDate)
            imageView.load(pantryItem.pantryPhotoUri)
        }

    }
}