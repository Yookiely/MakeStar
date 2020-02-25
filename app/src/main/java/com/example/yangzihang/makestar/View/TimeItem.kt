package com.example.yangzihang.makestar.View

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class TimeItem(val time : String)  : Item{
    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as TimeItemViewHolder
            item as TimeItem

            holder.title.text = item.time

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_message_ime, parent, false)
            return TimeItemViewHolder(view)

        }


    }



    override val controller: ItemController
        get() = TimeItem

    private class TimeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.message_time)
    }
}

fun MutableList<Item>.addTime(time: String) = add(TimeItem(time))