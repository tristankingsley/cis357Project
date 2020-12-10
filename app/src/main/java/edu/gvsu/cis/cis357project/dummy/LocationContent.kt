package edu.gvsu.cis.cis357project.dummy

import java.util.ArrayList

object LocationContent {

    val ITEMS: MutableList<LocationItem> = ArrayList()

    fun addItem(item: LocationItem) {
        ITEMS.add(item)
    }

    data class LocationItem(val lat: String, val long: String,
                            val date: String, val address: String
    )
    {
        override fun toString(): String {
            return "$lat , $long"
        }
    }
}