package com.wingedvampires.homepage.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.example.common.experimental.extensions.awaitAndHandle
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.HomePageService
import com.wingedvampires.homepage.model.HomePageUtils
import com.wingedvampires.homepage.model.Work
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.layoutInflater

class HomePageItem(val work: Work, val block: (View) -> Unit) : Item {
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
                Glide.with(this.itemView).load(work.avatar).error(R.drawable.ms_no_pic).into(avatar)
                Glide.with(this.itemView).load(work.cover_url).error(R.drawable.ms_no_pic).into(cover)
                title.text = work.work_name
                message.text = work.Introduction
                hotPerson.text = HomePageUtils.format(work.hot_value)
                label.text = HomePageUtils.typeList[work.work_type_ID]
            }
            holder.hot.setOnClickListener { view ->
                launch(UI + QuietCoroutineExceptionHandler) {
                    val commbody = HomePageService.star(work.work_ID).awaitAndHandle {
                        it.printStackTrace()
                        Toast.makeText(view.context, "点赞失败", Toast.LENGTH_SHORT).show()
                    }

                    Toast.makeText(view.context, "${commbody?.message}", Toast.LENGTH_SHORT).show()
                    val numText = holder.hotPerson.text
                    holder.hotPerson.text = HomePageUtils.format(commbody?.data?.numberOfStar) ?: numText

                }
            }

            holder.itemView.setOnClickListener {
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

fun MutableList<Item>.homePageItem(work: Work, block: (View) -> Unit = { _ -> }) = add(HomePageItem(work, block))