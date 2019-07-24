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

class RankItem(val rankNum : String,val rankTrend : Boolean,val ImgUrl : String, val nickName: String ,val heat : String) : Item{
    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as RankItemViewHolder
            item as RankItem
            holder.itemView.setOnClickListener{}

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_discover_rank,parent,false)
            return RankItemViewHolder(view)
        }

    }

    private class RankItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankNum = itemView.findViewById<TextView>(R.id.rank_num)
        val rankTrend = itemView.findViewById<ImageView>(R.id.rank_trend)
        val userRankImg = itemView.findViewById<ImageView>(R.id.user_rank_img)
        val userRankNickname = itemView.findViewById<TextView>(R.id.user_rank_nickname)
        val userRankHeat = itemView.findViewById<TextView>(R.id.user_rank_heat)
    }

    override val controller: ItemController
        get() = RankItem
}