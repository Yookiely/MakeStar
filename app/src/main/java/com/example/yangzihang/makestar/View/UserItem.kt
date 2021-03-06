package com.example.yangzihang.makestar.View

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.*
import org.jetbrains.anko.layoutInflater

class UserItem(val titles: String, val context: Context, val activity: Int,val flag : Int) : Item {
    lateinit var intent: Intent


    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as UserItemViewHolder
            item as UserItem
            holder.title.text = item.titles
            if (item.flag==1){
                holder.flag.visibility = View.VISIBLE
            }
            holder.itemView.setOnClickListener {
                item.startActivity(item.context, item.activity)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.user_item_my, parent, false)
            return UserItemViewHolder(view)

        }


    }

    fun startActivity(context: Context, activity: Int) {

        when (activity) {
            1 -> intent = Intent(context, RankhistoryActivity::class.java)
            2 -> intent = Intent(context, MessageActivity::class.java)
            3 -> intent = Intent(context,CollectionActivity::class.java)
            4 -> intent = Intent(context, FansHomeActivity::class.java)
            5 -> intent = Intent(context,SettingsActivity::class.java)
            7 -> intent = Intent(context, RedPacketActivity::class.java)
            8 -> intent = Intent(context, EditActivity::class.java)
        }


        context.startActivity(intent)
    }

    override val controller: ItemController
        get() = UserItem

    private class UserItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.user_text)
        val flag : CardView = itemView.findViewById(R.id.meassage_flag)
    }

}


fun MutableList<Item>.setUserText(titles: String, context: Context, activity: Int,flag : Int) =
    add(UserItem(titles, context, activity,flag))