package com.example.yangzihang.makestar.View

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.bumptech.glide.Glide
import com.example.yangzihang.makestar.ComplaintActivity
import com.example.yangzihang.makestar.PostBodyActivity
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.UserActiveData
import com.yookie.common.experimental.extensions.ComplaintType
import org.jetbrains.anko.image
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.windowManager
import org.jetbrains.anko.wrapContent

class MyFansCircleInfoItem(val context: Context, val fansCircleData: UserActiveData) : Item {
    private companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as MyFansCircleViewHolder
            item as MyFansCircleInfoItem
            val metrics = DisplayMetrics()
            item.context.windowManager.defaultDisplay.getMetrics(metrics)
            val width = metrics.widthPixels
            val imgList = ArrayList<String>()
            item.fansCircleData.img_urls.forEach {
                imgList.add(it)
            }
            holder.apply {
                itemView.setOnClickListener {
                    val intent = Intent(item.context, PostBodyActivity::class.java)
                    intent.putExtra("name", item.fansCircleData.username)
                    intent.putExtra("time", item.fansCircleData.time)
                    intent.putExtra("text", item.fansCircleData.content)
                    intent.putExtra("avatar", item.fansCircleData.avatar)
                    intent.putExtra("fandomid", item.fansCircleData.action_ID.toString())
                    intent.putStringArrayListExtra("imgs", imgList)
                    item.context.startActivity(intent)
                }
                Glide.with(this.itemView)
                    .load(item.fansCircleData.avatar)
                    .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                    .into(image)
                name.text = item.fansCircleData.username
                time.text = item.fansCircleData.time
                text.text = item.fansCircleData.content
                complain.setOnClickListener {
                    val intent = Intent(item.context, ComplaintActivity::class.java)
                    intent.putExtra(ComplaintType.COMPLAINT_TYPE, ComplaintType.LINE)
                    intent.putExtra(
                        ComplaintType.COMPLAINT_ID,
                        item.fansCircleData.action_ID.toString()
                    )
                    it.context.startActivity(intent)
                }

                when (imgList.size) {
                    0 -> {
                        singImage.image=null
                        singImage.layoutParams.height=0
                        douImage.layoutParams.height = 0
                        douImage2.layoutParams.height = 0
                        triImage.layoutParams.height=   0
                        triImage2.layoutParams.height=  0
                        triImage3.layoutParams.height=  0
                        tri2Image.layoutParams.height=  0
                        tri2Image2.layoutParams.height= 0
                        tri2Image3.layoutParams.height= 0
                        tri3Image.layoutParams.height=  0
                        tri3Image2.layoutParams.height= 0
                        tri3Image3.layoutParams.height= 0
                        singImage.visibility = View.INVISIBLE
                        imgList.clear()
                    }
                    1 -> {
                        singImage.visibility = View.VISIBLE
//                        singImage.layoutParams.height= 720
                        Glide.with(this.itemView)
                            .load(item.fansCircleData.img_urls[0])
                            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                            .into(singImage)
                        douImage.image=null
                        douImage2.image=null
                        triImage  .image=null
                        triImage2 .image=null
                        triImage3 .image=null
                        tri2Image .image=null
                        tri2Image2.image=null
                        tri2Image3.image=null
                        tri3Image .image=null
                        tri3Image2.image=null
                        tri3Image3.image=null
                    }
                    2 -> {
                            singImage .image=null
                            triImage  .image=null
                            triImage2 .image=null
                            triImage3 .image=null
                            tri2Image .image=null
                            tri2Image2.image=null
                            tri2Image3.image=null
                            tri3Image .image=null
                            tri3Image2.image=null
                            tri3Image3.image=null
                        singImage.visibility = View.INVISIBLE
                        Glide.with(this.itemView)
                            .load(item.fansCircleData.img_urls[0])
//                            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                            .into(douImage)
//                        douImage.layoutParams.width = width/2
//                        douImage2.layoutParams.width = width/2
                        Glide.with(this.itemView)
                            .load(item.fansCircleData.img_urls[1])
//                            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                            .into(douImage2)
                    }
                    in 3..9 -> {
                        singImage.image=null
                        douImage.image=null
                        douImage2.image=null
//                        triImage.layoutParams.width = width/3
//                        triImage2.layoutParams.width = width/3
//                        triImage3.layoutParams.width = width/3
//                        tri2Image.layoutParams.width = width/3
//                        tri2Image2.layoutParams.width = width/3
//                        tri2Image3.layoutParams.width = width/3
//                        tri3Image.layoutParams.width = width/3
//                        tri3Image2.layoutParams.width = width/3
//                        tri3Image3.layoutParams.width = width/3
                        Glide.with(this.itemView)
                            .load(item.fansCircleData.img_urls[0])
                            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                            .into(triImage)
                        Glide.with(this.itemView)
                            .load(item.fansCircleData.img_urls[1])
                            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                            .into(triImage2)
                        Glide.with(this.itemView)
                            .load(item.fansCircleData.img_urls[2])
//                            .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                            .into(triImage3)
                        if (item.fansCircleData.img_urls.size >= 4) {
                            Glide.with(this.itemView)
                                .load(item.fansCircleData.img_urls[3])
//                                .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                                .into(tri2Image)
                        }
                        if (item.fansCircleData.img_urls.size >= 5) {
                            Glide.with(this.itemView)
                                .load(item.fansCircleData.img_urls[4])
//                                .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                                .into(tri2Image2)
                        }
                        if (item.fansCircleData.img_urls.size >= 6) {
                            Glide.with(this.itemView)
                                .load(item.fansCircleData.img_urls[5])
//                                .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                                .into(tri2Image3)
                        }
                        if (item.fansCircleData.img_urls.size >= 7) {
                            Glide.with(this.itemView)
                                .load(item.fansCircleData.img_urls[6])
//                                .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                                .into(tri3Image)
                        }
                        if (item.fansCircleData.img_urls.size >= 8) {
                            Glide.with(this.itemView)
                                .load(item.fansCircleData.img_urls[7])
//                                .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                                .into(tri3Image2)
                        }
                        if (item.fansCircleData.img_urls.size >= 9) {
                            Glide.with(this.itemView)
                                .load(item.fansCircleData.img_urls[8])
//                                .error(com.wingedvampires.homepage.R.drawable.ms_no_pic)
                                .into(tri3Image3)
                        }
                    }
                }
//                rec.withItems(items)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_fans_circleinfo, parent, false)
            return MyFansCircleViewHolder(view)
        }

        private class MyFansCircleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val image = itemView.findViewById<ImageView>(R.id.fans_info_portrait)
            val name = itemView.findViewById<TextView>(R.id.fans_info_name)
            val time = itemView.findViewById<TextView>(R.id.fans_info_time)
            val text = itemView.findViewById<TextView>(R.id.fans_info_text)
            val singImage = itemView.findViewById<ImageView>(R.id.fans_info_photo)
            val douImage = itemView.findViewById<ImageView>(R.id.fans_dou_photo)
            val douImage2 = itemView.findViewById<ImageView>(R.id.fans_dou_photo)
            val triImage = itemView.findViewById<ImageView>(R.id.fans_tri_photo)
            val triImage2 = itemView.findViewById<ImageView>(R.id.fans_tri_photo2)
            val triImage3 = itemView.findViewById<ImageView>(R.id.fans_tri_photo3)
            val tri2Image = itemView.findViewById<ImageView>(R.id.fans_tri2_photo)
            val tri2Image2 = itemView.findViewById<ImageView>(R.id.fans_tri2_photo2)
            val tri2Image3 = itemView.findViewById<ImageView>(R.id.fans_tri2_photo3)
            val tri3Image = itemView.findViewById<ImageView>(R.id.fans_tri3_photo)
            val tri3Image2 = itemView.findViewById<ImageView>(R.id.fans_tri3_photo2)
            val tri3Image3 = itemView.findViewById<ImageView>(R.id.fans_tri3_photo3)
            val complain: TextView = itemView.findViewById(R.id.fans_text_info_complain)
        }

    }

    override val controller: ItemController
        get() = MyFansCircleInfoItem
}