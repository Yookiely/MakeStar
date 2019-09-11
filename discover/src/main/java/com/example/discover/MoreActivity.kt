package com.example.discover

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.discover.network.UserRank
import com.example.discover.view.addRankItem
import com.example.discover.view.addVideoItem
import kotlinx.android.synthetic.main.activity_more.*

class MoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)
        more_rec.layoutManager = LinearLayoutManager(this)
        more_rec.withItems {
            val num = intent.getIntExtra("flag", 2)
            if (num == 0) {
                UserRank.getUserRank(50) {
                    for ((index, value) in it.withIndex()) {
                        addRankItem(index.toString(), value, this@MoreActivity, this@MoreActivity)
                    }
                }
            } else if (num == 1) {
                UserRank.getWorkRank(50){
                    for ((index, value) in it.withIndex()) {
                        addVideoItem(value,index+1)
                    }

                }
            }
        }
    }
}
