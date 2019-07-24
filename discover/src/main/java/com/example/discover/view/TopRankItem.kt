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

class TopRankItem : Item {

    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as VedioItem
            item as RankItem
            holder.itemView.setOnClickListener {}

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_discover_toprank, parent, false)
            return TopRankItemViewHolder(view)
        }

    }

    private class TopRankItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstImg = itemView.findViewById<ImageView>(R.id.rank_img_first)
        val secondImg = itemView.findViewById<ImageView>(R.id.rank_img_second)
        val thirdImg = itemView.findViewById<ImageView>(R.id.rank_img_third)
        val firstNickname = itemView.findViewById<TextView>(R.id.rank_text_first)
        val secondNickname = itemView.findViewById<TextView>(R.id.rank_text_second)
        val thirdNickname = itemView.findViewById<TextView>(R.id.rank_text_third)
        val firstheat = itemView.findViewById<TextView>(R.id.rank_heat_first)
        val secondheat = itemView.findViewById<TextView>(R.id.rank_heat_second)
        val thirdheat = itemView.findViewById<TextView>(R.id.rank_heat_third)

    }

    override val controller: ItemController
        get() = TopRankItem

}