package com.example.yangzihang.makestar.View

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.*
import org.jetbrains.anko.layoutInflater

class GreyItem( val title : String) : Item{

    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as GreyItemViewHolder
            item as GreyItem

            holder.title.text = item.title

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.settings_item_grey, parent, false)
            return GreyItemViewHolder(view)

        }


    }



    override val controller: ItemController
        get() = GreyItem

    private class GreyItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.settings_grey_text)
    }
}

fun MutableList<Item>.setGreyText(title: String) = add(GreyItem(title))