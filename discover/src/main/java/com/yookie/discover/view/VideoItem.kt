package com.yookie.discover.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.yookie.discover.R
import com.yookie.discover.network.workData
import org.jetbrains.anko.layoutInflater

class VideoItem(val work : workData,val rank : Int) : Item{

    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as VedioItemViewHolder
            item as VideoItem
            holder.apply {
                title.text = item.work.work_name
                nickname.text = item.work.username
                heat.text = item.work.month_hot_value.toString()
//                time.text = item.work.
                rank.text = "No." + item.rank.toString()
                Glide.with(itemView)
                    .load(item.work.cover_url)
                    .into(cover)
                time.text = item.work.time
                add.visibility = View.GONE
                //check tag
//                when(item.work.tag.size){
//                    0 -> {
//                        tag1.visibility = View.GONE
//                        tag2.visibility = View.GONE
//                        tag3.visibility = View.GONE
//                    }
//                    1 -> {
//                        tag1.text = item.work.tag[0]
//                        tag2.visibility = View.GONE
//                        tag3.visibility = View.GONE
//                    }
//                    2 -> {
//                        tag1.text = item.work.tag[0]
//                        tag2.text = item.work.tag[1]
//                        tag3.visibility = View.GONE
//                    }
//                    else -> {
//                        tag1.text = item.work.tag[0]
//                        tag2.text = item.work.tag[1]
//                    }
//                }
            }

            holder.itemView.setOnClickListener{}

        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_discover_vedio,parent,false)
            return VedioItemViewHolder(view)
        }

    }
    private class VedioItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.rank_text_title)
        val nickname = itemView.findViewById<TextView>(R.id.rank_text_nickname)
        val heat = itemView.findViewById<TextView>(R.id.rank_text_count)
        val time = itemView.findViewById<TextView>(R.id.rank_text_time)
        val rank = itemView.findViewById<TextView>(R.id.rank_text_num)
        val cover = itemView.findViewById<ImageView>(R.id.rank_img_cover)
        val add = itemView.findViewById<Button>(R.id.rank_button)
        val tag1 = itemView.findViewById<TextView>(R.id.rank_tag_1)
        val tag2 = itemView.findViewById<TextView>(R.id.rank_tag_2)
        val tag3 = itemView.findViewById<TextView>(R.id.rank_tag_3)
    }

    override val controller: ItemController
        get() = VideoItem
}

fun MutableList<Item>.addVideoItem(work: workData,rank: Int) = add(VideoItem(work, rank))