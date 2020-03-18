package com.wingedvampires.attention.view.items

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.ConcernPerson
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.layoutInflater

class FocusItem(val concernPerson: ConcernPerson, val context: Context, val block: (View) -> Unit) : Item {
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

            val tags = concernPerson.tags?.split(",")

            holder.apply {
                Glide.with(this.itemView).load(concernPerson.avatar).error(R.drawable.ms_no_pic).into(avatar)
                name.text = concernPerson.username
                rank.text = "No.${concernPerson.month_rank}"
                message.text = (concernPerson.signature ?: "")
                // 设置标签
                tags?.forEachWithIndex { index, tag ->
                    when (index) {
                        1 -> label1.apply {
                            text = tag
                            visibility = View.VISIBLE
                        }
                        2 -> label2.apply {
                            text = tag
                            visibility = View.VISIBLE
                        }
                        3 -> label3.apply {
                            text = tag
                            visibility = View.VISIBLE
                        }
                        4 -> label4.apply {
                            text = tag
                            visibility = View.VISIBLE
                        }
                        5 -> label5.apply {
                            text = tag
                            visibility = View.VISIBLE
                        }
                    }
                }
            }

            holder.itemView.setOnClickListener {
                item.block(it)
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
    }
}

fun MutableList<Item>.focusItem(concernPerson: ConcernPerson, context: Context, block: (View) -> Unit = { _ -> }) =
    add(FocusItem(concernPerson, context, block))