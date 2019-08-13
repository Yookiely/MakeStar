package com.wingedvampires.attention.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.VideoAction
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.layoutInflater

class VideoActionItem(val videoAction: VideoAction, val block: (View) -> Unit) : Item {
    override val controller: ItemController
        get() = Controller

    override fun areContentsTheSame(newItem: Item): Boolean {
        return videoAction.work_ID == (newItem as? VideoActionItem)?.videoAction?.work_ID
    }

    override fun areItemsTheSame(newItem: Item): Boolean {
        return videoAction.work_ID == (newItem as? VideoActionItem)?.videoAction?.work_ID
    }

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_attention, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as VideoActionItem
            holder as ViewHolder

            val videoAction = item.videoAction

            holder.apply {
                Glide.with(this.itemView).load(videoAction.avatar).error(R.drawable.ms_no_pic).into(avatar)
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.cv_attention_pic)
        val name: TextView = itemView.findViewById(R.id.tv_attention_name)
        val title: TextView = itemView.findViewById(R.id.tv_attention_title)
        val rank: TextView = itemView.findViewById(R.id.tv_attention_rank)
        val time: TextView = itemView.findViewById(R.id.tv_attention_time)
        val complain: TextView = itemView.findViewById(R.id.tv_attention_complain)
        val complainMark: ImageView = itemView.findViewById(R.id.iv_attention_complain)
        val cover: ImageView = itemView.findViewById(R.id.iv_attention_video)
        val start: ImageView = itemView.findViewById(R.id.iv_attention_start)
        val duration: TextView = itemView.findViewById(R.id.tv_attention_duration)
        val label: TextView = itemView.findViewById(R.id.tv_attention_label)
        val number: TextView = itemView.findViewById(R.id.tv_attention_number)
        val shareImg: ImageView = itemView.findViewById(R.id.iv_attention_share)
        val shareNum: ImageView = itemView.findViewById(R.id.tv_attention_share)
        val storeImg: ImageView = itemView.findViewById(R.id.iv_attention_store)
        val storeNum: ImageView = itemView.findViewById(R.id.tv_attention_store)
        val commentImg: ImageView = itemView.findViewById(R.id.iv_attention_comment)
        val commentNum: ImageView = itemView.findViewById(R.id.tv_attention_comment)
    }
}

fun MutableList<Item>.videoActionItem(videoAction: VideoAction, block: (View) -> Unit = { _ -> }) =
    add(VideoActionItem(videoAction, block))