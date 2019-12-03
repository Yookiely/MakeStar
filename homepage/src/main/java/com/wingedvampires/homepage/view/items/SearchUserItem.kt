package com.wingedvampires.homepage.view.items

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.DataOfUser
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.layoutInflater

class SearchUserItem(val dataOfUser: DataOfUser, val block: (View) -> Unit) : Item {
    override val controller: ItemController
        get() = Controller

    override fun areContentsTheSame(newItem: Item): Boolean {
        return dataOfUser.user_ID == (newItem as? SearchUserItem)?.dataOfUser?.user_ID
    }

    override fun areItemsTheSame(newItem: Item): Boolean {
        return dataOfUser.user_ID == (newItem as? SearchUserItem)?.dataOfUser?.user_ID
    }

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_search_user, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SearchUserItem

            val dataOfUser = item.dataOfUser

            holder.apply {
                name.text = dataOfUser.username
                Glide.with(this.itemView.context).load(dataOfUser.avatar).error(R.drawable.ms_no_pic)
                    .into(avatar)
            }

            holder.itemView.setOnClickListener {
                item.block(it)
            }
        }


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.iv_search_avatar)
        val name: TextView = itemView.findViewById(R.id.tv_search_username)
    }
}

fun MutableList<Item>.searchUserItem(dataOfUser: DataOfUser, block: (View) -> Unit = { }) =
    add(SearchUserItem(dataOfUser, block))