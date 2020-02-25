package com.example.yangzihang.makestar.View

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater


class AccountItem( val texts : String,val action: () -> Unit) : Item {

    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as AccountItemViewHolder
            item as NormalSetItem
            holder.title.text = item.texts
            holder.itemView.setOnClickListener {
                item.action()
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_set_account, parent, false)
            return AccountItemViewHolder(view)

        }

    }



    override val controller: ItemController
        get() = AccountItem

    private class AccountItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.settings_account_text)
    }
}


fun MutableList<Item>.addAccount(texts: String,action: () -> Unit) = add(AccountItem(texts, action))