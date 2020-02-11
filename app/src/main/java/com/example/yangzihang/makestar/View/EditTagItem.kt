package com.example.yangzihang.makestar.View

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class EditTagItem(
    val context: Context,
    val tagContent: String,
    val select: Boolean,
    val index: Int,
    val list: MutableList<Int>
) : Item {
    var isSelect = false

    override val controller: ItemController
        get() = Controller


    companion object Controller : ItemController {

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_edit_tag, parent, false)

            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as EditTagItem

            item.isSelect = item.select

            holder.apply {
                tag.text = item.tagContent

                if (item.isSelect) {
                    tag.setBackgroundResource(R.drawable.ms_tag_select)
                    tag.setTextColor(ContextCompat.getColor(item.context, R.color.redTag))
                    item.list.add(item.index)
                } else {
                    tag.setBackgroundResource(R.drawable.ms_tag_noselect)
                    tag.setTextColor(ContextCompat.getColor(item.context, R.color.greyTag))
                    item.list.remove(item.index)
                }

                itemView.setOnClickListener {
                    if (item.isSelect) {
                        tag.setBackgroundResource(R.drawable.ms_tag_noselect)
                        tag.setTextColor(ContextCompat.getColor(item.context, R.color.greyTag))
                        item.isSelect = false
                        item.list.remove(item.index)
                    } else {
                        tag.setBackgroundResource(R.drawable.ms_tag_select)
                        tag.setTextColor(ContextCompat.getColor(item.context, R.color.redTag))
                        item.isSelect = true
                        item.list.add(item.index)
                    }
                }
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tag: TextView = itemView.findViewById(R.id.tv_edit_tag)
    }
}

fun MutableList<Item>.editTagItem(
    context: Context,
    tagContent: String,
    select: Boolean,
    index: Int,
    list: MutableList<Int>
) = add(EditTagItem(context, tagContent, select, index, list))