package com.example.yangzihang.makestar.View


import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.rank
import org.jetbrains.anko.layoutInflater

class RankHistoryItem(val rank: rank) :Item{
    private companion object Controller : ItemController {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as RankHistoryItemViewHolder
            item as RankHistoryItem
            holder.apply {
                month.text = item.rank.month + "月"
                rank.text = "No." + item.rank.rank
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_rankhistory_cardview, parent, false)
            return RankHistoryItemViewHolder(view)

        }



    }

    override val controller: ItemController
        get() = RankHistoryItem

    private class RankHistoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val month = itemView.findViewById<TextView>(R.id.rankhistory_month)
        val rank = itemView.findViewById<TextView>(R.id.rank_rank)
    }
}

fun MutableList<Item>.addRankHistory(rank: rank) = add(RankHistoryItem(rank))