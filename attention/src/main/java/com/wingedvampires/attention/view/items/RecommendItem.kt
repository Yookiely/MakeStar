package com.wingedvampires.attention.view.items

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.example.common.experimental.extensions.awaitAndHandle
import com.wingedvampires.attention.R
import com.wingedvampires.attention.model.AttentionService
import com.wingedvampires.attention.model.RecommendUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.layoutInflater

class RecommendItem(val recommendUser: RecommendUser, val block: (View) -> Unit) : Item {
    override val controller: ItemController
        get() = Controller

    override fun areContentsTheSame(newItem: Item): Boolean {
        return recommendUser.user_ID == (newItem as? RecommendItem)?.recommendUser?.user_ID
    }

    override fun areItemsTheSame(newItem: Item): Boolean {
        return recommendUser.user_ID == (newItem as? RecommendItem)?.recommendUser?.user_ID
    }

    companion object Controller : ItemController {
        var havaAdd = false
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_attention_recommend, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as RecommendItem
            holder as ViewHolder
            val recommendUser = item.recommendUser
            val tags = recommendUser.tags.split(",")

            holder.apply {

                Glide.with(this.itemView).load(recommendUser.avatar).error(R.drawable.ms_no_pic).into(avatar)
                name.text = recommendUser.username
                message.text = (recommendUser.signature ?: "")
                rank.text = "No.${recommendUser.month_rank}"

                add.setOnClickListener {
                    if (havaAdd) {
                        launch(UI + QuietCoroutineExceptionHandler) {
                            val addCommonBody = AttentionService.deleteFollow(recommendUser.user_ID).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(holder.itemView.context, "操作失败", Toast.LENGTH_SHORT).show()
                            } ?: return@launch

                            Toast.makeText(holder.itemView.context, addCommonBody.message, Toast.LENGTH_SHORT).show()

                            if (addCommonBody.error_code == -1) {
                                add.text = "+关注"
                                havaAdd = false
                            }
                        }
                    } else {
                        launch(UI + QuietCoroutineExceptionHandler) {
                            val addCommonBody = AttentionService.addFollow(recommendUser.user_ID).awaitAndHandle {
                                it.printStackTrace()
                                Toast.makeText(holder.itemView.context, "操作失败", Toast.LENGTH_SHORT).show()
                            } ?: return@launch

                            Toast.makeText(holder.itemView.context, addCommonBody.message, Toast.LENGTH_SHORT).show()

                            if (addCommonBody.error_code == -1) {
                                add.text = "取消关注"
                                havaAdd = true
                            }
                        }
                    }

                }

                tags.forEachWithIndex { index, tag ->
                    when (index) {
                        1 -> label1.apply {
                            text = tag
                            visibility = View.VISIBLE
                        }
                        2 -> label2.apply {
                            text = tag
                            visibility = View.VISIBLE
                        }
                        3 -> label3.apply {
                            text = tag
                            visibility = View.VISIBLE
                        }
                    }
                }
            }
            holder.itemView.setOnClickListener {
                item.block(it)
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: CircleImageView = itemView.findViewById(R.id.cv_attention_pic_recommend)
        val name: TextView = itemView.findViewById(R.id.tv_attention_name_recommend)
        val message: TextView = itemView.findViewById(R.id.tv_attention_message_recommend)
        val rank: TextView = itemView.findViewById(R.id.tv_attention_rank_recommend)
        val add: TextView = itemView.findViewById(R.id.tv_attention_add_recommend)
        val label1: TextView = itemView.findViewById(R.id.tv_attention_recommend_label1)
        val label2: TextView = itemView.findViewById(R.id.tv_attention_recommend_label2)
        val label3: TextView = itemView.findViewById(R.id.tv_attention_recommend_label3)
    }
}

fun MutableList<Item>.recommendItem(recommendUser: RecommendUser, block: (View) -> Unit = { _ -> }) =
    add(RecommendItem(recommendUser, block))