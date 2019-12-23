package com.example.yangzihang.makestar.View

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.FansSeCommentActivity
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.FansCommentText
import com.example.yangzihang.makestar.network.FansSeCommentText
import org.jetbrains.anko.layoutInflater

class FansCommentItem(val context: Context,val fansComment:FansCommentText?,val fansSeCommentText: FansSeCommentText?): Item {

    private companion object  Controller : ItemController {
        var isSecond =false
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as FansCommentViewHolder
            item as FansCommentItem
            val fansComment =item.fansComment
            val fansSeComment = item.fansSeCommentText
            isSecond = (item.fansComment == null)
            val avatarUrl =  if(isSecond) fansSeComment?.avatar else fansComment?.avatar
            holder.apply {
                Glide.with(this.itemView)
                    .load(avatarUrl)
                    .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                    .into(image)
                val moreNum = if(!isSecond) fansComment?.second_level_comment_num else 0
                name.text = if(!isSecond) fansComment?.username else fansSeComment?.username
                content.text =if(!isSecond) fansComment?.fac_content else fansSeComment?.facc_content
                more.text = "共${moreNum}条回复 >>"
                time.text=if(!isSecond) fansComment?.time else fansSeComment?.time
            }
            if (isSecond){
                holder.more.visibility = View.INVISIBLE
            }
            holder.more.setOnClickListener {
                val intent = Intent(item.context,FansSeCommentActivity::class.java)
                intent.putExtra("facid",fansComment!!.facID)
                item.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_fans_comment, parent, false)
            return FansCommentViewHolder(view)
        }
    }
    override val controller: ItemController
        get() = FansCommentItem
    private class FansCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.fans_comment_avatar)
        val name = itemView.findViewById<TextView>(R.id.fans_comment_name)
        val content = itemView.findViewById<TextView>(R.id.fans_comment_context)
        val more =itemView.findViewById<TextView>(R.id.fans_comment_more)
        val time =itemView.findViewById<TextView>(R.id.fans_comment_time)
    }
}
fun MutableList<Item>.addFansComment( context: Context,fansComment: FansCommentText? ,fansSeCommentText: FansSeCommentText?) = add(FansCommentItem(context,fansComment,fansSeCommentText))