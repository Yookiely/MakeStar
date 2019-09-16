package com.yookie.discover.view

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.yookie.discover.MoreActivity
import com.yookie.discover.R
import org.jetbrains.anko.layoutInflater

class MoreItem(val context: Context,val num: Int) : Item {
    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as MoreItemViewHolder
            item as MoreItem
            holder.moreText.setOnClickListener {
                val intent = Intent(item.context, MoreActivity::class.java)
                intent.putExtra("flag",item.num)
                item.context.startActivity(intent)
            }
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

fun MutableList<Item>.addMoreItem(context: Context,num: Int) = add(MoreItem(context,num))