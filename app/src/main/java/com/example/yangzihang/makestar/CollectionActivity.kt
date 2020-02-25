package com.example.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.View.addCollection
import com.example.yangzihang.makestar.network.UserImp
import com.yookie.common.experimental.preference.CommonPreferences

class CollectionActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var itemManager : ItemManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        recyclerView = findViewById(R.id.my_collection_rec)
        val mlayoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = mlayoutManager
        itemManager = ItemManager()
        recyclerView.adapter = ItemAdapter(itemManager)
        refreshList(1)

//        UserImp.getCollection(100,1,CommonPreferences.userid){
//            recyclerView.withItems {
//                for (x in it){
//                    addCollection(x,this@CollectionActivity)
//                }
//
//
//            }
//        }
    }

    private fun refreshList(num : Int){
        UserImp.getCollection(10,num,CommonPreferences.userid){
            itemManager.autoRefresh {
                for (x in it.data){
                    addCollection(x,this@CollectionActivity)
                }
            }
            if (it.lastPage>num){
                refreshList(it.currentPage+1)
            }
        }
    }
}
