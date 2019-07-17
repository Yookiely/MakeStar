package com.example.userpage.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.userpage.R
import org.jetbrains.anko.layoutInflater

class UserItem(val titles : String) : Item{


    private companion object Controller : ItemController{
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as UserItemViewHolder
            item  as UserItem
            holder.title.text = item.titles
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.user_item_my, parent, false)
            return UserItemViewHolder(view)

        }

        private class UserItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val title = itemView.findViewById<TextView>(R.id.user_text)
        }

    }


    override val controller: ItemController
        get() = UserItem


}


fun MutableList<Item>.setUserText(titles: String) = add(UserItem(titles))