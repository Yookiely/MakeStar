package com.wingedvampires.attention.view.items

import android.app.AlertDialog
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
import com.yookie.common.experimental.preference.CommonPreferences
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.AttentionUtils
import com.wingedvampires.attention.model.Comment
import com.wingedvampires.attention.model.SecondComment
import com.wingedvampires.attention.view.SecondCommentActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity


class CommentItem(
    val context: Context,
    val showLine: Boolean,
    val comment: Comment?,
    val secondComment: SecondComment?,
    val showMore: Boolean,
    val block: (CommentItem) -> Unit
) : Item {

    override val controller: ItemController
        get() = Controller

    companion object Controller : ItemController {
        var isSecond = false

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_comment_detail, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as CommentItem
            holder as ViewHolder
            val comment = item.comment
            val secondComment = item.secondComment
            isSecond = (comment == null)

            val avatarUrl = if (isSecond) secondComment?.avatar else comment?.avatar
            val contentText = if (isSecond) secondComment?.content else comment?.content
            val nameText = if (isSecond) secondComment?.username else comment?.username
            val timeText = if (isSecond) secondComment?.time else comment?.time
            val userId = if (isSecond) secondComment?.user_ID else comment?.user_ID
            val commentId = if (isSecond) secondComment?.ccID else comment?.comment_ID
            val store = 0
            val moreNum = if (isSecond) 0 else comment?.comment_comment


            holder.apply {

                Glide.with(this.itemView).load(avatarUrl).error(R.drawable.ms_no_pic)
                    .into(avatar)
                name.text = nameText
                content.text = contentText
                time.text = timeText
                commentImg.visibility = if (isSecond) View.GONE else View.VISIBLE
                shareImg.visibility = if (isSecond) View.GONE else View.VISIBLE
                more.apply {
                    text = "共${moreNum}条回复 >>"
                    visibility = if (!item.showMore) View.GONE else View.VISIBLE
                }
                storeText.text = store.toString()
                bottomLine.visibility = if (item.showLine) View.VISIBLE else View.GONE
            }

            holder.more.setOnClickListener {
                it.context.startActivity<SecondCommentActivity>(AttentionUtils.SECOND_COMMENT_INDEX to comment!!.comment_ID)
            }

            holder.itemView.setOnLongClickListener {
                val items: ArrayList<String> = arrayListOf("举报了举报了！", "我要删它！", "手瓢点错了，取消叭").also {
                    if (userId != CommonPreferences.userid) {
                        it.removeAt(1)
                    }
                }

                val listDialog = AlertDialog.Builder(item.context)
                    .setItems(items.toArray(arrayOf<String>())) { dialog, which ->
                        // which 下标从0开始
                        // ...To-do
                        when (which) {
                            0 -> Toast.makeText(item.context, "举报了", Toast.LENGTH_SHORT).show()
                            (items.size - 1) -> {
                            }
                            else -> {
                                launch(UI + QuietCoroutineExceptionHandler) {
                                    val result = if (isSecond) {
                                        AttentionService.deleteSecondComment(commentId!!)
                                    } else {
                                        AttentionService.deleteComment(commentId!!)
                                    }.awaitAndHandle {
                                        it.printStackTrace()
                                        Toast.makeText(item.context, "删除失败", Toast.LENGTH_SHORT)
                                            .show()
                                    } ?: return@launch

                                    Toast.makeText(item.context, result.message, Toast.LENGTH_SHORT)
                                        .show()
                                    if (result.error_code == -1) {
                                        item.block(item)
                                    }
                                }
                            }
                        }
                    }

                listDialog.show()
                true
            }

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.cv_comment_pic)
        val name: TextView = itemView.findViewById(R.id.tv_comment_name)
        val time: TextView = itemView.findViewById(R.id.tv_comment_time)
        val shareImg: ImageView = itemView.findViewById(R.id.iv_comment_share)
        val storeImg: ImageView = itemView.findViewById(R.id.iv_comment_like)
        val storeText: TextView = itemView.findViewById(R.id.tv_comment_like)
        val commentImg: ImageView = itemView.findViewById(R.id.iv_comment_comment)
        val content: TextView = itemView.findViewById(R.id.tv_comment_context)
        val more: TextView = itemView.findViewById(R.id.tv_comment_more)
        val bottomLine: View = itemView.findViewById(R.id.v_comment_main)
    }

}

fun MutableList<Item>.commentItem(
    context: Context,
    showLine: Boolean,
    comment: Comment? = null,
    secondComment: SecondComment? = null,
    showMore: Boolean = true,
    block: (CommentItem) -> Unit = {}
) = add(CommentItem(context, showLine, comment, secondComment, showMore, block))