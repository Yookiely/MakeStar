package com.example.yangzihang.makestar.View

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.example.yangzihang.makestar.EditDetailActivity
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class EditTagInputItem(
    val editDetailActivity: EditDetailActivity,
    val tagContent: String,
    val select: Boolean
) : Item {
    var isSelect = false
    var tagText: String? = null

    override val controller: ItemController
        get() = Controller


    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_edit_tag_input, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as EditTagInputItem

            item.isSelect = item.select

            holder.apply {
                inputTag.setText(item.tagContent)

                if (item.isSelect) {
                    inputTag.apply {
                        setBackgroundResource(R.drawable.ms_tag_select)
                        setTextColor(
                            ContextCompat.getColor(
                                item.editDetailActivity,
                                R.color.redTag
                            )
                        )
                    }
                    item.tagText = inputTag.text.toString()
                } else {
                    inputTag.apply {
                        setBackgroundResource(R.drawable.ms_tag_noselect)
                        setTextColor(
                            ContextCompat.getColor(
                                item.editDetailActivity,
                                R.color.greyTag
                            )
                        )
                    }
                    item.tagText = null
                }

                itemView.setOnClickListener {
                    if (item.isSelect) {
                        inputTag.apply {
                            setBackgroundResource(R.drawable.ms_tag_noselect)
                            setTextColor(
                                ContextCompat.getColor(
                                    item.editDetailActivity,
                                    R.color.greyTag
                                )
                            )
                        }

                        item.isSelect = false
                        item.tagText = null
                        item.decLabel()
                    } else {
                        if (item.editDetailActivity.totalSelected < 6) {
                            inputTag.apply {
                                setBackgroundResource(R.drawable.ms_tag_select)
                                setTextColor(
                                    ContextCompat.getColor(
                                        item.editDetailActivity,
                                        R.color.redTag
                                    )
                                )
                            }

                            item.isSelect = true
                            item.tagText = inputTag.text.toString()
                            if (inputTag.text.toString().isBlank()) {
                                item.tagText = null
                            }
                            item.incLabel()
                        }
                    }
                }


                inputTag.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) = Unit

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) = Unit

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        item.tagText = inputTag.text.toString()
                        if (inputTag.text.toString().isBlank()) {
                            item.tagText = null
                        }
                    }
                })
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
        val inputTag: EditText = itemView.findViewById(R.id.et_edit_tag)
    }
}