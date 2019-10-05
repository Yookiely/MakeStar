package com.wingedvampires.homepage.view.items

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.wingedvampires.homepage.R
import org.jetbrains.anko.layoutInflater

class SearchTitleItem(val title: String, val block: (View) -> Unit) : Item {

    override val controller: ItemController
        get() = Controller


    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_search_title, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as SearchTitleItem
            holder as ViewHolder

            holder.apply {
                title.text = item.title

                moreText.setOnClickListener {
                    item.block(it)
                }
                moreImg.setOnClickListener {
                    item.block(it)
                }
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_search_title)
        val moreText: TextView = itemView.findViewById(R.id.tv_search_more)
        val moreImg: ImageView = itemView.findViewById(R.id.iv_search_more)
    }
}

fun MutableList<Item>.searchTitleItem(title: String, block: (View) -> Unit = {}) =
    add(SearchTitleItem(title, block))