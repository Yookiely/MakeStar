package com.example.yangzihang.makestar.View

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.EditDetailActivity
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class EditTagItem(
    val editDetailActivity: EditDetailActivity,
    val tagContent: String,
    val select: Boolean,
    val list: MutableList<String>
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
                    tag.apply {
                        setBackgroundResource(R.drawable.ms_tag_select)
                        setTextColor(
                            ContextCompat.getColor(
                                item.editDetailActivity,
                                R.color.redTag
                            )
                        )
                    }

                    item.list.add(item.tagContent)
                } else {
                    tag.apply {
                        setBackgroundResource(R.drawable.ms_tag_noselect)
                        setTextColor(
                            ContextCompat.getColor(
                                item.editDetailActivity,
                                R.color.greyTag
                            )
                        )
                    }

                    item.list.remove(item.tagContent)
                }

                itemView.setOnClickListener {
                    if (item.isSelect) {
                        tag.apply {
                            setBackgroundResource(R.drawable.ms_tag_noselect)
                            setTextColor(
                                ContextCompat.getColor(
                                    item.editDetailActivity,
                                    R.color.greyTag
                                )
                            )
                        }

                        item.isSelect = false
                        item.list.remove(item.tagContent)
                        item.decLabel()

                    } else {
                        if (item.editDetailActivity.totalSelected < 6) {
                            tag.apply {
                                setBackgroundResource(R.drawable.ms_tag_select)
                                setTextColor(
                                    ContextCompat.getColor(
                                        item.editDetailActivity,
                                        R.color.redTag
                                    )
                                )
                            }

                            item.isSelect = true
                            item.list.add(item.tagContent)
                            item.incLabel()
                        }
                    }
                }
            }
        }

    }

    fun decLabel() {
        this.editDetailActivity.apply {
            totalSelected--
            refreshTagLabel()
        }
    }

    fun incLabel() {
        this.editDetailActivity.apply {
            totalSelected++
            refreshTagLabel()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tag: TextView = itemView.findViewById(R.id.tv_edit_tag)
    }
}

fun MutableList<Item>.editTagItem(
    editDetailActivity: EditDetailActivity,
    tagContent: String,
    select: Boolean,
    list: MutableList<String>
) = add(EditTagItem(editDetailActivity, tagContent, select, list))