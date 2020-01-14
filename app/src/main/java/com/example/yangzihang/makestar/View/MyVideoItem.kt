package com.example.yangzihang.makestar.View

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.MyVideo
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import org.jetbrains.anko.layoutInflater

class MyVideoItem(val work : MyVideo, val activity: Activity) :Item{
    private companion object Controller : ItemController {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as MyVideoItemViewHolder
            item as MyVideoItem
            holder.apply {
                Glide.with(item.activity)
                    .load(item.work.cover_url)
                    .into(cover)
                text.text = item.work.hot_value.toString()
            }
            holder.itemView.setOnClickListener {
                val intent = Intent().also { it.putExtra("videopalyWorkId", item.work.video_ID) }
                Transfer.startActivityWithoutClose(item.activity, "VideoPlayActivity", intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_myself_video, parent, false)
            return MyVideoItemViewHolder(view)
        }



    }

    override val controller: ItemController
        get() = MyVideoItem

    private class MyVideoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover = itemView.findViewById<ImageView>(R.id.myself_cover)
        val text = itemView.findViewById<TextView>(R.id.myself_num)
    }

}

fun MutableList<Item>.addMyVideo(work: MyVideo,activity: Activity) = add(MyVideoItem(work,activity))