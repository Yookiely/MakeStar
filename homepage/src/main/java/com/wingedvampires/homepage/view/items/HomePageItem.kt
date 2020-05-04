package com.wingedvampires.homepage.view.items

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
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.HomePageUtils
import com.wingedvampires.homepage.model.Work
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.layoutInflater

class HomePageItem(val activity: Activity, val work: Work, val block: (View) -> Unit) : Item {
    override val controller: ItemController
        get() = Controller

    override fun areContentsTheSame(newItem: Item): Boolean {
        return work.work_ID == (newItem as? HomePageItem)?.work?.work_ID && work.work_type_ID == (newItem as? HomePageItem)?.work?.work_type_ID
    }

    override fun areItemsTheSame(newItem: Item): Boolean {
        return work.work_ID == (newItem as? HomePageItem)?.work?.work_ID && work.work_type_ID == (newItem as? HomePageItem)?.work?.work_type_ID
    }

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_homepage, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as HomePageItem

            val work = item.work

            holder.apply {
                Glide.with(this.itemView)
                    .load(work.avatar)
                    .error(R.drawable.ms_no_pic)
                    .into(avatar)
                Glide.with(this.itemView)
                    .load(work.cover_url)
                    .placeholder(R.drawable.ms_no_pic)
                    .error(R.drawable.ms_no_pic)
                    .into(cover)
                title.text = work.work_name
                message.text = work.Introduction
                hotPerson.text = HomePageUtils.format(work.hot_value.toString())
                label.text = HomePageUtils.typeList[work.work_type_ID]
            }

            holder.avatar.setOnClickListener {
                val intent = Intent()
                intent.putExtra("userID", work.user_ID.toString())
                Transfer.startActivityWithoutClose(item.activity, "MyselfActivity", intent)
            }

            holder.cover.setOnClickListener {
                item.block(it)
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.iv_homepage_video)
        val avatar: CircleImageView = itemView.findViewById(R.id.iv_homepage_person)
        val title: TextView = itemView.findViewById(R.id.tv_homepage_title)
        val message: TextView = itemView.findViewById(R.id.tv_homepage_message)
        val label: TextView = itemView.findViewById(R.id.tv_homepage_item_label)
        val hotPerson: TextView = itemView.findViewById(R.id.tv_homepage_hotperson)
        val hot: ImageView = itemView.findViewById(R.id.iv_homepage_fire)
    }
}

fun MutableList<Item>.homePageItem(
    activity: Activity,
    work: Work,
    block: (View) -> Unit = { _ -> }
) = add(
    HomePageItem(
        activity,
        work,
        block
    )
)