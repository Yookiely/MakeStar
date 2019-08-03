package com.wingedvampires.homepage.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.Work
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.layoutInflater

class HomePageItem(val work: Work, val block: () -> Unit) : Item {
    override val controller: ItemController
        get() = Controller

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_homepage, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as HomePageItem

            val work = item.work

            holder.apply {

            }

            holder.itemView.setOnClickListener {
                item.block()
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val video: ImageView = itemView.findViewById(R.id.iv_homepage_video)
        val avatar: CircleImageView = itemView.findViewById(R.id.iv_homepage_person)
        val title: TextView = itemView.findViewById(R.id.tv_homepage_title)
        val message: TextView = itemView.findViewById(R.id.tv_homepage_message)
        val hotPerson: TextView = itemView.findViewById(R.id.tv_homepage_hotperson)
        val hot: ImageView = itemView.findViewById(R.id.iv_homepage_fire)
    }
}

fun MutableList<Item>.homePageItem(work: Work, block: () -> Unit = {}) = add(HomePageItem(work, block))