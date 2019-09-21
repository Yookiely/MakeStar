package com.yookie.discover.view

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.yookie.discover.MoreActivity
import com.yookie.discover.R
import com.yookie.discover.network.acData
import com.yookie.discover.network.activityData
import org.jetbrains.anko.layoutInflater

class ActivityItem(val activityData: acData,val activity: Activity) :Item{
    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ActivityItemViewHolder
            item as ActivityItem
            holder.itemView.setOnClickListener{
                val intent = Intent(item.activity,MoreActivity::class.java)
                item.activity.startActivity(intent)
            }
            item.activityData.apply {
                Glide.with(item.activity)
                    .load(cover_url)
                    .into(holder.cover)
                Glide.with(item.activity)
                    .load(the_star_avatar)
                    .into(holder.Img)
                holder.nickname.text = the_star_username
                holder.title.text = activity_name
                holder.progressnum.text = progress

            }


        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_donate_active,parent,false)
            return ActivityItemViewHolder(view)
        }

    }

    private class ActivityItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Img = itemView.findViewById<ImageView>(R.id.user_donate_img)
        val buuton = itemView.findViewById<Button>(R.id.activity_focus)
        val nickname = itemView.findViewById<TextView>(R.id.user_donate_nickname)
        val cover = itemView.findViewById<ImageView>(R.id.activity_cover)
        val title = itemView.findViewById<TextView>(R.id.act_donate_title)
        val classification = itemView.findViewById<TextView>(R.id.act_donate_classification)
        val progressbar = itemView.findViewById<ProgressBar>(R.id.act_donate_progressbar)
        val progressnum = itemView.findViewById<TextView>(R.id.act_donate_progressnum)


    }

    override val controller: ItemController
        get() = ActivityItem

}

fun MutableList<Item>.addActivityItem(activityData: acData,activity: Activity) = add(ActivityItem(activityData,activity))