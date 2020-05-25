package com.example.yangzihang.makestar.View

import android.annotation.SuppressLint
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
import com.example.yangzihang.makestar.FansActivity
import com.example.yangzihang.makestar.R
import org.jetbrains.anko.layoutInflater

class MyCircleItem(val imgUrl :String, val userName :String, val userID : Int, val context: Context): Item {
    private companion object Controller : ItemController {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
        holder as MyCircleItemViewHolder
        item as MyCircleItem
        holder.apply {
            Glide.with(this.itemView)
                .load(item.imgUrl)
                .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                .into(image)
            name.text = item.userName
        }
        holder.itemView.setOnClickListener{
            val intent = Intent(item.context, FansActivity::class.java)
            intent.putExtra("userID",item.userID.toString())
            intent.putExtra("avatar",item.imgUrl)
            item.context.startActivity(intent)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = parent.context.layoutInflater
        val view = inflater.inflate(R.layout.item_fans_circle, parent, false)
        return MyCircleItemViewHolder(view)

    }

}

    override val controller: ItemController
        get() = MyCircleItem
    private class MyCircleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.fans_home_portrait)
        val name = itemView.findViewById<TextView>(R.id.fans_home_name)
    }
}
fun MutableList<Item>.addMycircle( imgUrl :String , userName :String , userID: Int , context: Context) = add(
    MyCircleItem(imgUrl, userName , userID,context)
)