package com.wingedvampires.attention.view.items

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.model.WorkById
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.ShareMethod
import com.yookie.common.experimental.extensions.awaitAndHandle
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.layoutInflater

class VideoActionCommentItem(val workById: WorkById, val context: Context) : Item {
    var isCollected = false

    override val controller: ItemController
        get() = Controller

    companion object Controller : ItemController {


        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_comment_main, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as VideoActionCommentItem
            holder as ViewHolder

            val workById = item.workById
            item.isCollected = workById.is_collected

            holder.apply {
                Glide.with(this.itemView).load(workById.avatar).error(R.drawable.ms_no_pic)
                    .into(avatar)
                Glide.with(this.itemView).load(workById.cover_url).error(R.drawable.ms_no_pic)
                    .into(cover)
                time.text = workById.time
                introdection.text = workById.Introduction
                commentNum.text = AttentionUtils.format(workById.comment_num)
                storeNum.text = AttentionUtils.format(workById.collection_num)
                shareNum.text = AttentionUtils.format(workById.share_num)
                storeImg.apply {
                    if (item.isCollected)
                        setImageResource(R.drawable.ms_red_star)
                    else
                        setImageResource(R.drawable.ms_star)
                }

                storeImg.setOnClickListener {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        if (!item.isCollected) {
                            val resultCommonBody =
                                AttentionService.addCollection(workById.work_ID).awaitAndHandle {
                                    it.printStackTrace()
                                    Toast.makeText(
                                        item.context,
                                        "收藏失败：${it.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } ?: return@launch

                            Toast.makeText(
                                item.context,
                                resultCommonBody.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            if (resultCommonBody.error_code == -1) {
                                storeImg.setImageResource(R.drawable.ms_red_star)
                                item.isCollected = true
                            }
                        } else {
                            val resultCommonBody =
                                AttentionService.deleteCollection(workById.work_ID).awaitAndHandle {
                                    it.printStackTrace()
                                    Toast.makeText(
                                        item.context,
                                        "收藏失败：${it.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } ?: return@launch

                            Toast.makeText(
                                item.context,
                                resultCommonBody.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            if (resultCommonBody.error_code == -1) {
                                storeImg.setImageResource(R.drawable.ms_star)
                                item.isCollected = false
                            }
                        }
                    }
                }

                shareImg.setOnClickListener {
                    ShareMethod.showDialog(
                        item.context,
                        workById.work_ID,
                        workById.work_name,
                        workById.username
                    )
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.cv_comment_pic)
        val name: TextView = itemView.findViewById(R.id.tv_comment_name)
        val introdection: TextView = itemView.findViewById(R.id.tv_comment_title)
        val time: TextView = itemView.findViewById(R.id.tv_comment_time)
        val complain: TextView = itemView.findViewById(R.id.tv_comment_complain)
        val complainMark: ImageView = itemView.findViewById(R.id.iv_comment_complain)
        val cover: ImageView = itemView.findViewById(R.id.iv_comment_video)
        val shareImg: ImageView = itemView.findViewById(R.id.iv_comment_share)
        val shareNum: TextView = itemView.findViewById(R.id.tv_comment_share)
        val storeImg: ImageView = itemView.findViewById(R.id.iv_comment_store)
        val storeNum: TextView = itemView.findViewById(R.id.tv_comment_store)
        val commentImg: ImageView = itemView.findViewById(R.id.iv_comment_comment)
        val commentNum: TextView = itemView.findViewById(R.id.tv_comment_comment)
    }

}

fun MutableList<Item>.videoActionCommentItem(workById: WorkById, context: Context) =
    add(VideoActionCommentItem(workById, context))