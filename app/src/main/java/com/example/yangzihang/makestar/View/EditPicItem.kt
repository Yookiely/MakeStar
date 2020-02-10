package com.example.yangzihang.makestar.View

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.R
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.layoutInflater

class EditPicItem(
    val title: String,
    val context: Context,
    val path: String,
    val block: (EditPicItem) -> Unit
) : Item {
    private lateinit var mHolder: ViewHolder
    override val controller: ItemController
        get() = Controller

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_edit_avatar, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as EditPicItem

            item.mHolder = holder
            holder.apply {
                title.text = item.title
                Glide.with(item.context).load(item.path).into(pic)

                more.setOnClickListener {
                    item.block(item)
                }

                pic.setOnClickListener {
                    item.block(item)
                }
            }
        }
    }

    fun loadAnUploadPic(path: String) {
        launch(UI + QuietCoroutineExceptionHandler) {


        }
        Glide.with(context).load(path).error(R.drawable.ms_no_pic).into(mHolder.pic)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_edit_avatar_title)
        val pic: CircleImageView = itemView.findViewById(R.id.civ_edit_avatar_person)
        val more: ImageView = itemView.findViewById(R.id.iv_edit_avatar_more)
    }
}

fun MutableList<Item>.editPicItem(
    title: String,
    context: Context,
    path: String,
    block: (EditPicItem) -> Unit
) =
    add(EditPicItem(title, context, path, block))