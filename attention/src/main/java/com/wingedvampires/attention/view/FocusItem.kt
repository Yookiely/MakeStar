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

class FocusItem(val concernPerson: ConcernPerson, val block: (View) -> Unit) : Item {
    override val controller: ItemController
        get() = Controller

    override fun areContentsTheSame(newItem: Item): Boolean {
        return concernPerson.user_ID == (newItem as? FocusItem)?.concernPerson?.user_ID
    }

    override fun areItemsTheSame(newItem: Item): Boolean {
        return concernPerson.user_ID == (newItem as? FocusItem)?.concernPerson?.user_ID
    }

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_attention_focus, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as FocusItem
            holder as ViewHolder
            val concernPerson = item.concernPerson

            holder.apply {

            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.cv_attention_pic_focus)
        val name: TextView = itemView.findViewById(R.id.tv_attention_name_focus)
        val message: TextView = itemView.findViewById(R.id.tv_attention_message_focus)
        val rank: TextView = itemView.findViewById(R.id.tv_attention_rank_focus)
        val label1: TextView = itemView.findViewById(R.id.tv_attention_focus_label1)
        val label2: TextView = itemView.findViewById(R.id.tv_attention_focus_label2)
        val label3: TextView = itemView.findViewById(R.id.tv_attention_focus_label3)
        val label4: TextView = itemView.findViewById(R.id.tv_attention_focus_label4)
        val label5: TextView = itemView.findViewById(R.id.tv_attention_focus_label5)
        val time: TextView = itemView.findViewById(R.id.tv_attention_time_focus)
    }
}

fun MutableList<Item>.focusItem(concernPerson: ConcernPerson, block: (View) -> Unit = { _ -> }) =
    add(FocusItem(concernPerson, block))