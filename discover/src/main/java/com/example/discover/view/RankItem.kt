package com.example.discover.view

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.discover.R
import com.example.discover.network.userData
import org.jetbrains.anko.layoutInflater

class RankItem(val num : String,val userData: userData,val context : Context,val activity: Activity) : Item{
    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as RankItemViewHolder
            item as RankItem
            holder.itemView.setOnClickListener{}
            holder.apply {
                rankNum.text = item.num
                Glide.with(item.activity)
                    .load(item.userData.avatar)
                    .into(userRankImg)
                userRankNickname.text = item.userData.username
                userRankHeat.text = item.userData.week_hot_value.toString()

            }

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

fun MutableList<Item>.addRankItem(num: String,userData: userData,context: Context,activity: Activity) = add(RankItem(num,userData,context,activity))