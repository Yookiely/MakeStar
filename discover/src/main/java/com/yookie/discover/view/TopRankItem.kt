package com.yookie.discover.view

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
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.discover.R
import com.yookie.discover.network.userData
import org.jetbrains.anko.layoutInflater

class TopRankItem(val userList: List<userData>,val activity : Activity) : Item {

    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as TopRankItemViewHolder
            item as TopRankItem
            holder.apply {
                firstNickname.text = item.userList[0].username
                secondNickname.text = item.userList[1].username
                thirdNickname.text = item.userList[2].username
                firstheat.text = item.userList[0].month_hot_value.toString()
                secondheat.text = item.userList[1].month_hot_value.toString()
                thirdheat.text = item.userList[2].month_hot_value.toString()
                Glide.with(itemView)
                    .load(item.userList[0].avatar)
                    .into(firstImg)

                Glide.with(itemView)
                    .load(item.userList[1].avatar)
                    .into(secondImg)

                Glide.with(itemView)
                    .load(item.userList[2].avatar)
                    .into(thirdImg)

                firstImg.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra("userID",item.userList[0].user_ID.toString())
                    Transfer.startActivityWithoutClose(item.activity,"MyselfActivity",intent)

                }

                secondImg.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra("userID",item.userList[1].user_ID.toString())
                    Transfer.startActivityWithoutClose(item.activity,"MyselfActivity",intent)
                }

                thirdImg.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra("userID",item.userList[2].user_ID.toString())
                    Transfer.startActivityWithoutClose(item.activity,"MyselfActivity",intent)
                }

            }



        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_discover_toprank, parent, false)
            return TopRankItemViewHolder(view)
        }

    }

    private class TopRankItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstImg = itemView.findViewById<ImageView>(R.id.rank_img_first)
        val secondImg = itemView.findViewById<ImageView>(R.id.rank_img_second)
        val thirdImg = itemView.findViewById<ImageView>(R.id.rank_img_third)
        val firstNickname = itemView.findViewById<TextView>(R.id.rank_text_first)
        val secondNickname = itemView.findViewById<TextView>(R.id.rank_text_second)
        val thirdNickname = itemView.findViewById<TextView>(R.id.rank_text_third)
        val firstheat = itemView.findViewById<TextView>(R.id.rank_heat_first)
        val secondheat = itemView.findViewById<TextView>(R.id.rank_heat_second)
        val thirdheat = itemView.findViewById<TextView>(R.id.rank_heat_third)

    }

    override val controller: ItemController
        get() = TopRankItem

}

fun MutableList<Item>.addTopRankItem(userList: List<userData>,activity: Activity) = add(TopRankItem(userList,activity))