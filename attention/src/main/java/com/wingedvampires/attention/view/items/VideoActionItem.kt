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
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.model.VideoAction
import com.wingedvampires.attention.view.CommentsActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity

class VideoActionItem(
    val videoAction: VideoAction,
    val context: Context,
    val block: (View) -> Unit
) : Item {
    override val controller: ItemController
        get() = Controller

    override fun areContentsTheSame(newItem: Item): Boolean {
        return videoAction.work_ID == (newItem as? VideoActionItem)?.videoAction?.work_ID
    }

    override fun areItemsTheSame(newItem: Item): Boolean {
        return videoAction.work_ID == (newItem as? VideoActionItem)?.videoAction?.work_ID
    }

    companion object Controller : ItemController {
        var isCollected = false

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(
                R.layout.item_attention_videoaction,
                parent,
                false
            )

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as VideoActionItem
            holder as ViewHolder

            val videoAction = item.videoAction
            var labelText = ""
            videoAction.tags.split(",").forEach {
                labelText += "#$it      "
            }
            isCollected = videoAction.is_collected

            holder.apply {
                Glide.with(this.itemView).load(videoAction.avatar).error(R.drawable.ms_no_pic)
                    .into(avatar)
                Glide.with(this.itemView).load(videoAction.cover_url).error(R.drawable.ms_no_pic)
                    .into(cover)
                name.text = videoAction.username
                time.text = videoAction.time.split(" ")[0]
                title.text = videoAction.work_name
                duration.text = AttentionUtils.formatTime(videoAction.Duration)
                rank.text = videoAction.month_rank.toString()
                commentNum.text = AttentionUtils.format(videoAction.comment_num)
                storeNum.text = AttentionUtils.format(videoAction.collection_num)
                shareNum.text = AttentionUtils.format(videoAction.share_num)
                number.text = AttentionUtils.format(videoAction.hot_value)

                label.text = labelText
                shareImg.apply {
                    if (isCollected)
                        setImageResource(R.drawable.ms_red_star)
                    else
                        setImageResource(R.drawable.ms_star)
                }

                storeImg.setOnClickListener {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        if (!isCollected) {
                            val resultCommonBody =
                                AttentionService.addCollection(videoAction.work_ID).awaitAndHandle {
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
                                shareImg.setImageResource(R.drawable.ms_red_star)
                                isCollected = true
                            }
                        } else {
                            val resultCommonBody =
                                AttentionService.deleteCollection(videoAction.work_ID).awaitAndHandle {
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
                                shareImg.setImageResource(R.drawable.ms_star)
                                isCollected = false
                            }
                        }
                    }
                }

                start.setOnClickListener {
                    item.block(it)
                }

                commentImg.setOnClickListener {
                    it.context.startActivity<CommentsActivity>(AttentionUtils.COMMENT_INDEX to videoAction.work_ID)
                }
            }

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
    val shareNum: TextView = itemView.findViewById(R.id.tv_attention_share)
    val storeImg: ImageView = itemView.findViewById(R.id.iv_attention_store)
    val storeNum: TextView = itemView.findViewById(R.id.tv_attention_store)
    val commentImg: ImageView = itemView.findViewById(R.id.iv_attention_comment)
    val commentNum: TextView = itemView.findViewById(R.id.tv_attention_comment)
}


fun MutableList<Item>.videoActionItem(
    videoAction: VideoAction,
    context: Context,
    block: (View) -> Unit = { _ -> }
) =
    add(VideoActionItem(videoAction, context, block))