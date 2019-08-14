package com.example.yangzihang.makestar.View

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.MainActivity
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class UserItem(val titles: String, val context: Context, val activity: Int) : Item {
    lateinit var intent: Intent


    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as UserItemViewHolder
            item as UserItem
            holder.title.text = item.titles
            holder.itemView.setOnClickListener {
                item.startActivity(item.context,item.activity)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.user_item_my, parent, false)
            return UserItemViewHolder(view)

        }

        private class UserItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title = itemView.findViewById<TextView>(R.id.user_text)
        }

    }

    fun startActivity(context: Context,activity: Int){

        when(activity){
            1 -> intent = Intent(context,MainActivity::class.java)
        }


        context.startActivity(intent)
    }
    override val controller: ItemController
        get() = UserItem


}


fun MutableList<Item>.setUserText(titles: String, context: Context, activity: Int) =
    add(UserItem(titles, context, activity))