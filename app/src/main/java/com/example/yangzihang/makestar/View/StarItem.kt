package com.example.yangzihang.makestar.View

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class StarItem(val FLAG : Int ,val avater : String,val nicknames : String , val time : String , val comment : String ,val quote : String) : Item {
    private companion object Controller : ItemController {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as StarItemViewHolder
            item as StarItem
            holder.apply {
                Glide.with(itemView)
                    .load(item.avater)
                    .into(cover)
                nickname.text = item.nicknames
                time.text = item.time
                when(item.FLAG){
                     1 -> action.text = "回复了你"
                     0 -> action.text = "给你点了个赞"
                }
                comment.text = item.comment
                quote.text = "\"" +item.quote
            }



        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_up_view, parent, false)
            return StarItemViewHolder(view)

        }


    }


    override val controller: ItemController
        get() = StarItem

    private class StarItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val  cover = itemView.findViewById<ImageView>(R.id.user_portrait)
        val  nickname = itemView.findViewById<TextView>(R.id.user_nickname)
        val  time = itemView.findViewById<TextView>(R.id.user_time)
        val action = itemView.findViewById<TextView>(R.id.user_action)
        val comment = itemView.findViewById<TextView>(R.id.user_comment)
        val quote = itemView.findViewById<TextView>(R.id.user_quote)



    }

}

fun MutableList<Item>.addStarItem(FLAG: Int,avater: String,nicknames: String,time: String,comment: String,quote: String) = add(StarItem(FLAG, avater, nicknames, time, comment, quote))
