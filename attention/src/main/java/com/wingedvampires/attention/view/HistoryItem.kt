package com.wingedvampires.attention.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.DataOfUser
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.layoutInflater

class HistoryItem(val dataOfUser: DataOfUser, val block: () -> Unit) : Item {
    override val controller: ItemController
        get() = RecommendItem

    override fun areContentsTheSame(newItem: Item): Boolean {
        return dataOfUser.user_ID == (newItem as? HistoryItem)?.dataOfUser?.user_ID
    }

    override fun areItemsTheSame(newItem: Item): Boolean {
        return dataOfUser.user_ID == (newItem as? HistoryItem)?.dataOfUser?.user_ID
    }

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_attention_history, parent, false)

            return RecommendItem.ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as HistoryItem

            val dataOfUser = item.dataOfUser

            holder.apply {
                name.text = dataOfUser.username
                Glide.with(this.itemView).load(dataOfUser.avatar).error(R.drawable.ms_no_pic).into(avatar)
            }
        }


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.cv_attention_pic_history)
        val name: TextView = itemView.findViewById(R.id.tv_attention_name_history)
    }
}