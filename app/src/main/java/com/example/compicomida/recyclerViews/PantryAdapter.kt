package com.example.compicomida.recyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.db.entities.PantryItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PantryAdapter(

    private var pantryItems: List<PantryItem>,
    private val onClickGoToItemDetail: (Int?) -> Unit,

) : RecyclerView.Adapter<PantryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemLayout = R.layout.recycler_pantry
        val view =
            LayoutInflater.from(viewGroup.context).inflate(itemLayout, viewGroup, false)
        return ViewHolder(view, onClickGoToItemDetail)
    }

    override fun getItemCount() = pantryItems.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(pantryItems[position])
    }

    class ViewHolder(
        view: View,
        onClickGoToItemDetail: (Int?) -> Unit,
    ) : RecyclerView.ViewHolder(view) {


        private val tvTitle: TextView = view.findViewById(R.id.recycler_pantry_item_title)
        private val tvText: TextView = view.findViewById(R.id.recycler_pantry_item_text)
        private val tvExpirationDate: TextView = view.findViewById(R.id.recycler_pantry_item_expiration_date)
        private val imageView: ImageView = view.findViewById(R.id.recycler_pantry_item_image)
//        private val btnDeleteItem: Button =
//            view.findViewById(R.id.recycler_pantry_item_btn_delete)

        private var pantryItem: PantryItem? = null

        init {
            view.setOnClickListener { onClickGoToItemDetail(pantryItem?.itemId) }
        }

        fun bind(pantryItem: PantryItem) {
            this.pantryItem = pantryItem

            tvTitle.text = pantryItem.pantryName
            tvText.text = parseUnitQuantity(pantryItem.unit, pantryItem.quantity)
            tvExpirationDate.text = parseDate(pantryItem.expirationDate)
            imageView.load(pantryItem.pantryPhotoUri)
        }

        private fun parseDate(expirationDate: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = expirationDate.format(formatter)
            return "Caduca el $formattedDate"
        }

        private fun parseUnitQuantity(unit: String?, quantity: Double): String {

            // If unit is "No especificada" or NULL --> ""
            // If unit is other --> unit
            val unitParsed = if (unit != "No especificada" && unit != null) unit else ""

            // If quantity has no decimal --> to Integer (except for 0, not shown)
            // If unit has decimal --> as it is (except for 0.5, shown as 1/2)
            // other fractions can be considered...
            val quantityParsed = if (quantity.mod(1.0) == 0.0) {
                if (quantity.toInt() == 0) "-" else quantity.toInt().toString()
            } else {
                if (quantity == 0.5)
                    "1/2"
                else
                    quantity.toString()
            }

            return itemView.context.getString(
                R.string.grocery_items_adapter_cantidad_text,
                quantityParsed,
                unitParsed
            )
        }

    }
}