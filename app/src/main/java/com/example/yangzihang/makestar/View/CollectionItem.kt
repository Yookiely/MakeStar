package com.example.yangzihang.makestar.View

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.MyVideo
import com.example.yangzihang.makestar.network.UserImp
import com.example.yangzihang.makestar.network.UserService
import com.example.yangzihang.makestar.network.collectionData
import com.yookie.auth.LoginActivity
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.common.experimental.preference.CommonPreferences
import org.jetbrains.anko.layoutInflater

class CollectionItem(val work : collectionData, val activity: Activity) : Item {
    private companion object Controller : ItemController {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as CollectionItemViewHolder
            item as CollectionItem
            holder.apply {
                Glide.with(item.activity)
                    .load(item.work.cover_url)
                    .error(R.drawable.ms_no_pic)
                    .placeholder(R.drawable.ms_no_pic)
                    .into(cover)
                text.text = item.work.hot_value.toString()
            }
            holder.itemView.setOnClickListener {
                val intent = Intent().also { it.putExtra("videopalyWorkId", item.work.work_ID) }
                Transfer.startActivityWithoutClose(item.activity, "VideoPlayActivity", intent)
            }
            holder.itemView.setOnLongClickListener{
                AlertDialog.Builder(item.activity)
                    .setMessage("真的要删除吗？")
                    .setPositiveButton("真的") { _, _ ->
                        UserImp.deleteCollection(item.work.collection_ID){
                            Toast.makeText(item.activity, "删除成功请重新进入查看", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("算了") { _, _ ->
                        Toast.makeText(item.activity, "真爱啊 TAT...", Toast.LENGTH_SHORT).show()
                    }.create().show()
                    false
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_myself_video, parent, false)
            return CollectionItemViewHolder(view)
        }



    }

    override val controller: ItemController
        get() = CollectionItem

    private class CollectionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover = itemView.findViewById<ImageView>(R.id.myself_cover)
        val text = itemView.findViewById<TextView>(R.id.myself_num)
    }

}

fun MutableList<Item>.addCollection(work: collectionData, activity: Activity) = add(CollectionItem(work,activity))