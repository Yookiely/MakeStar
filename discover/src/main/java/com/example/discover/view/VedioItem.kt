package com.example.discover.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.discover.R
import org.jetbrains.anko.layoutInflater

class VedioItem : Item{

    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as VedioItemViewHolder
            item as VedioItem
            holder.itemView.setOnClickListener{}

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_discover_vedio,parent,false)
            return VedioItemViewHolder(view)
        }

    }
    private class VedioItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.rank_text_title)
        val nickname = itemView.findViewById<TextView>(R.id.rank_text_nickname)
        val heat = itemView.findViewById<TextView>(R.id.rank_text_count)
        val time = itemView.findViewById<TextView>(R.id.rank_text_time)
        val rank = itemView.findViewById<TextView>(R.id.rank_text_num)
        val cover = itemView.findViewById<ImageView>(R.id.rank_img_cover)
        val add = itemView.findViewById<Button>(R.id.rank_button)
        val tag1 = itemView.findViewById<TextView>(R.id.rank_tag_1)
        val tag2 = itemView.findViewById<TextView>(R.id.rank_tag_2)
        val tag3 = itemView.findViewById<TextView>(R.id.rank_tag_3)
    }

    override val controller: ItemController
        get() = VedioItem
}