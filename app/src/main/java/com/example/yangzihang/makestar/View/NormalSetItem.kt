package com.example.yangzihang.makestar.View

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class NormalSetItem( val texts : String,val action: () -> Unit) : Item{

    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as NormalSetItemViewHolder
            item as NormalSetItem
            if (item.texts=="退出登录"){
                holder.title.setTextColor(Color.parseColor("#cc0000"))
            }
            holder.title.text = item.texts
            holder.itemView.setOnClickListener {
                item.action()
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.settings_item_normal, parent, false)
            return NormalSetItemViewHolder(view)

        }


    }



    override val controller: ItemController
        get() = NormalSetItem

    private class NormalSetItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.settings_normal_text)
    }
}

fun MutableList<Item>.setNormalItem(texts: String,action: () -> Unit) = add(NormalSetItem(texts, action))