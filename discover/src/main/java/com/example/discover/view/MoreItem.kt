package com.example.discover.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.discover.R
import org.jetbrains.anko.layoutInflater

class MoreItem : Item {
    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as MoreItemViewHolder
            item as MoreItem
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_discover_more,parent,false)
            return MoreItemViewHolder(view)
        }
        private class MoreItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val moreText = itemView.findViewById<TextView>(R.id.more_text)
        }
    }


    override val controller: ItemController
        get() = MoreItem
}