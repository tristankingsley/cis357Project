package edu.gvsu.cis.cis357project

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import edu.gvsu.cis.cis357project.dummy.LocationContent

class MyItemRecyclerViewAdapter(
    private val values: List<LocationContent.LocationItem>,
    val listener: ((LocationContent.LocationItem) -> Unit)? = null
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(values[position], listener)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateView: TextView
        val parentView: View
        var item: LocationContent.LocationItem? = null
        val p1 : TextView
        val address: TextView

        override fun toString(): String {
            return super.toString() + " '" + p1.text + "'"
        }

        init {
            dateView = view.findViewById(R.id.timestamp)
            parentView = view
            p1 = view.findViewById<View>(R.id.p1) as TextView
            address = view.findViewById(R.id.address_label) as TextView
        }

        public fun bindTo(d: LocationContent.LocationItem, listener: ((LocationContent.LocationItem) -> Unit)?) {
            item = d
            p1.text = d.toString()
            dateView.text = d.date
            address.text = d.address
            if (listener != null) {
                parentView.setOnClickListener {
                    listener(d)
                }
            }
        }
    }
}