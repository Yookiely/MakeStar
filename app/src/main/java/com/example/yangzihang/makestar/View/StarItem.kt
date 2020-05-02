package com.example.yangzihang.makestar.View

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.R
import com.wingedvampires.attention.model.AttentionUtils
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import org.jetbrains.anko.layoutInflater

class StarItem(
    val activity: Activity,
    val fromUserID: String,
    val type: Int,
    val direct_ID: String,
    val FLAG: Int,
    val avater: String,
    val nicknames: String,
    val time: String,
    val comment: String,
    val quote: String,
    val isnew: Boolean
) : Item {
    private companion object Controller : ItemController {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as StarItemViewHolder
            item as StarItem
            holder.apply {
                Glide.with(itemView)
                    .load(item.avater)
                    .into(cover)
                nickname.text = item.nicknames
                time.text = item.time
                when(item.FLAG){
                     1 -> action.text = "回复了你"
                     0 -> action.text = "给你点了个赞"
                }
                comment.text = item.comment
                quote.text = "\"" +item.quote
                if (item.isnew){
                    flag.visibility = View.VISIBLE
                }else{
                    flag.visibility = View.GONE
                }

                cover.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra("userID", item.fromUserID)
                    Transfer.startActivityWithoutClose(item.activity, "MyselfActivity", intent)
                }
            }
            holder.itemView.setOnClickListener {
                /**
                 * 1是表示有人评论你的作品
                2是表示有人评论你的动态
                3是表示有人评论你对作品的评论
                4是表示有人评论你对动态的评论
                 */
                when (item.type) {
                    1, 3 -> {
                        val intent = Intent()
                        intent.putExtra(AttentionUtils.COMMENT_INDEX, item.direct_ID)
                        Transfer.startActivityWithoutClose(
                            item.activity,
                            "CommentsActivity",
                            intent
                        )
                    }
                    2, 4 -> {

                    }
                    100 -> {
                        val intent = Intent()
                        intent.putExtra(AttentionUtils.COMMENT_INDEX, item.direct_ID)
                        Transfer.startActivityWithoutClose(
                            item.activity,
                            "CommentsActivity",
                            intent
                        )
                    }

                }
            }



        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_up_view, parent, false)
            return StarItemViewHolder(view)

        }


    }


    override val controller: ItemController
        get() = StarItem

    private class StarItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val  cover = itemView.findViewById<ImageView>(R.id.user_portrait)
        val  nickname = itemView.findViewById<TextView>(R.id.user_nickname)
        val  time = itemView.findViewById<TextView>(R.id.user_time)
        val action = itemView.findViewById<TextView>(R.id.user_action)
        val comment = itemView.findViewById<TextView>(R.id.user_comment)
        val quote = itemView.findViewById<TextView>(R.id.user_quote)
        val flag = itemView.findViewById<CardView>(R.id.meassage_up_flag)



    }

}

fun MutableList<Item>.addStarItem(
    activity: Activity,
    fromUserID: String,
    type: Int,
    direct_ID: String,
    FLAG: Int,
    avater: String,
    nicknames: String,
    time: String,
    comment: String,
    quote: String,
    isnew: Boolean
) = add(
    StarItem(
        activity,
        fromUserID,
        type,
        direct_ID,
        FLAG,
        avater,
        nicknames,
        time,
        comment,
        quote,
        isnew
    )
)
