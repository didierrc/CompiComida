package com.example.compicomida.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil3.load
import com.example.compicomida.R

/**
 * Home Fragment:
 * - Shows the products near to expire from your pantry.
 * - Shows the products from the most recent shop list.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boxImage = view.findViewById<ImageView>(R.id.box_image)
        val listImage = view.findViewById<ImageView>(R.id.list_image)

        boxImage.load("https://s2.ppllstatics.com/eldiariomontanes/www/multimedia/202005/16/media/cortadas/55366113--1248x830.jpg")
        listImage.load("https://www.centrallecheraasturiana.es/nutricionysalud/wp-content/uploads/2021/03/bol-nueces.jpg")
    }


}