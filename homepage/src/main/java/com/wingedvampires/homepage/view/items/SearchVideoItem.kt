package com.wingedvampires.homepage.view.items

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.DataOfWork
import org.jetbrains.anko.layoutInflater

class SearchVideoItem(val work: DataOfWork, val block: (View) -> Unit) : Item {
    override val controller: ItemController
        get() = Controller

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_search_video, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as SearchVideoItem
            holder as ViewHolder
            val work = item.work
            holder.apply {
                title.text = work.work_name
                introduction.text = work.introduction
                time.text = work.time
                count.text = "${work.hot_value}次助力"
                Glide.with(this.itemView.context)
                    .load(work.cover_url)
                    .error(R.drawable.ms_no_pic)
                    .into(cover)
            }

            holder.itemView.setOnClickListener {
                item.block(it)
            }
        }

    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_search_videotitle)
        val introduction: TextView = itemView.findViewById(R.id.tv_search_videoname)
        val count: TextView = itemView.findViewById(R.id.tv_search_videocount)
        val time: TextView = itemView.findViewById(R.id.tv_search_videotime)
        val cover: ImageView = itemView.findViewById(R.id.iv_search_cover)

    }
}

fun MutableList<Item>.searchVideoItem(work: DataOfWork, block: (View) -> Unit = {}) =
    add((SearchVideoItem(work, block)))