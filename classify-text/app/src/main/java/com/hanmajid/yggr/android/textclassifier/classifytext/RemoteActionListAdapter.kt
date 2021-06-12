package com.hanmajid.yggr.android.textclassifier.classifytext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.RemoteActionCompat
import androidx.recyclerview.widget.RecyclerView


class RemoteActionListAdapter(
    private val context: Context,
    private val actions: List<RemoteActionCompat>,
) : RecyclerView.Adapter<RemoteActionListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayoutCompat = view.findViewById(R.id.container)
        val icon: ImageView = view.findViewById(R.id.image_icon)
        val title: TextView = view.findViewById(R.id.text_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_action, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val action = actions[position]
        holder.container.setOnClickListener {
            action.actionIntent.send()
        }
        if (action.shouldShowIcon()) {
            holder.icon.setImageDrawable(action.icon.loadDrawable(context))
        }
        holder.title.text = action.title
    }

    override fun getItemCount(): Int {
        return actions.size
    }
}