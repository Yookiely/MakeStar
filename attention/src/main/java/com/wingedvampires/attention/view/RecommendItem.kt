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

class RecommendItem(val concernPerson: ConcernPerson, val block: () -> Unit) : Item {
    override val controller: ItemController
        get() = Controller


    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_attention_recommend, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as RecommendItem
            holder as ViewHolder
            val concernPerson = item.concernPerson

            holder.apply {
                add.setOnClickListener {
                    item.block()
                }
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.cv_attention_pic_recommend)
        val name: TextView = itemView.findViewById(R.id.tv_attention_name_recommend)
        val message: TextView = itemView.findViewById(R.id.tv_attention_message_recommend)
        val rank: TextView = itemView.findViewById(R.id.tv_attention_rank_recommend)
        val add: TextView = itemView.findViewById(R.id.tv_attention_add_recommend)
        val label1: TextView = itemView.findViewById(R.id.tv_attention_recommend_label1)
        val label2: TextView = itemView.findViewById(R.id.tv_attention_recommend_label2)
        val label3: TextView = itemView.findViewById(R.id.tv_attention_recommend_label3)
    }
}

fun MutableList<Item>.recommendItem(concernPerson: ConcernPerson, block: () -> Unit = {}) =
    add(RecommendItem(concernPerson, block))