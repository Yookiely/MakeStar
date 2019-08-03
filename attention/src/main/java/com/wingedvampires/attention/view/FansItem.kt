package com.wingedvampires.attention.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.ConcernPerson
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.layoutInflater

class FansItem(val concernPerson: ConcernPerson, val block: () -> Unit) : Item {
    override val controller: ItemController
        get() = Controller


    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_attention_near, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as FansItem
            holder as ViewHolder
            val concernPerson = item.concernPerson

            holder.apply {
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.cv_attention_pic_near)
        val name: TextView = itemView.findViewById(R.id.tv_attention_name_near)
        val message: TextView = itemView.findViewById(R.id.tv_attention_message_near)
        val rank: TextView = itemView.findViewById(R.id.tv_attention_rank_near)
        val online: TextView = itemView.findViewById(R.id.tv_attention_status_near)
    }
}

fun MutableList<Item>.fansItem(concernPerson: ConcernPerson, block: () -> Unit = {}) =
    add(FansItem(concernPerson, block))