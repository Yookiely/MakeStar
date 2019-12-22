package com.example.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.View.FansCommentItem
import com.example.yangzihang.makestar.network.UserImp

class FansSeCommentActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager = ItemManager()
    private var isLoading = true
    private var page: Int = 1
    private var lastPage = Int.MAX_VALUE
    lateinit var secondCommitRefresh: SwipeRefreshLayout
    lateinit var commentId: String
    private var facId = 0
    var items = mutableListOf<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fans_second_comment)
        facId =intent.getIntExtra("facid",0)
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView=findViewById(R.id.fans_secondcomment_rec)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }
        loadSeComment()
    }
    private fun loadSeComment(){
        page= 1
        UserImp.getCommentCommentByAcID(1,4,page){     //假数据
            it.data.forEach {fansSecomment->
                items.add(FansCommentItem(this,null,fansSecomment))
            }
            recyclerView.withItems(items)
        }
    }
}
