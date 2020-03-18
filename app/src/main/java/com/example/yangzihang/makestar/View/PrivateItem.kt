package com.example.yangzihang.makestar.View

import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.LeaveMessageActivity
import com.example.yangzihang.makestar.MessageActivity
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.messageData
import org.jetbrains.anko.layoutInflater

class PrivateItem(val messagedata : messageData,val flag : Boolean)  : Item {
    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as PrivateItemViewHolder
            item as PrivateItem
            holder.apply {
                Glide.with(holder.itemView)
                    .load(item.messagedata.avatar)
                    .placeholder(R.drawable.ms_no_pic)
                    .error(R.drawable.ms_no_pic)
                    .into(avater)
                nickname.text = item.messagedata.username
                message.text = item.messagedata.content
                time.text = item.messagedata.time
                button.setOnClickListener {
                    val intent = Intent(itemView.context, LeaveMessageActivity::class.java)
                    intent.putExtra("userID",item.messagedata.from)
                    itemView.context.startActivity(intent)
                }
                if (item.flag){
                    flag.visibility = View.VISIBLE
                }
            }




        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_message_private, parent, false)
            return PrivateItemViewHolder(view)

        }


    }



    override val controller: ItemController
        get() = PrivateItem

    private class PrivateItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avater: ImageView = itemView.findViewById(R.id.message_avater)
        val nickname : TextView = itemView.findViewById(R.id.message_nickname)
        val time : TextView = itemView.findViewById(R.id.message_time)
        val message : TextView = itemView.findViewById(R.id.message_message)
        val button : TextView = itemView.findViewById(R.id.message_button)
        val flag : CardView = itemView.findViewById(R.id.meassage_private_flag)
    }
}

fun MutableList<Item>.addPrivate(messagedata : messageData,flag : Boolean) = add(PrivateItem(messagedata,flag))