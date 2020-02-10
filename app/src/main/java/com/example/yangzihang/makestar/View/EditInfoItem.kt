package com.example.yangzihang.makestar.View

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class EditInfoItem(
    val title: String,
    val content: String,
    val showMore: Boolean,
    val block: (View) -> Unit
) : Item {
    override val controller: ItemController
        get() = Controller

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_edit_info, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as EditInfoItem

            holder.apply {
                title.text = item.title
                content.text = item.content

                more.visibility = if (item.showMore) View.VISIBLE else View.GONE
                more.setOnClickListener {
                    item.block(it)
                }
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_edit_info_title)
        val content: TextView = itemView.findViewById(R.id.tv_edit_info_content)
        val more: ImageView = itemView.findViewById(R.id.iv_edit_info_more)
    }
}

fun MutableList<Item>.editInfoItem(
    title: String,
    content: String,
    showMore: Boolean,
    block: (View) -> Unit
) =
    add(EditInfoItem(title, content, showMore, block))