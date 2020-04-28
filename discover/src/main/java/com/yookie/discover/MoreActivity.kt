package com.yookie.discover

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.view.WindowManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.yookie.discover.network.UserRank
import com.yookie.discover.view.addRankItem
import com.yookie.discover.view.addVideoItem
import kotlinx.android.synthetic.main.activity_more.*

class MoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_more)
        iv_rank_back.setOnClickListener { onBackPressed() }
        val moreRec = findViewById<RecyclerView>(R.id.more_rec)
        moreRec.layoutManager = LinearLayoutManager(this)
        val num = intent.getIntExtra("flag", 2)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        if (num == 0) {
            UserRank.getUserRank(20) {
                moreRec.withItems {
                    for ((index, value) in it.withIndex()) {
                        addRankItem(
                            (index + 1).toString(),
                            value,
                            this@MoreActivity,
                            this@MoreActivity
                        )
                    }
                }
            }
        } else if (num == 1) {
            UserRank.getWorkRank(20) {
                moreRec.withItems {
                    for ((index, value) in it.withIndex()) {
                        addVideoItem(value, index + 1,this@MoreActivity)
                    }
                }

            }
        }
    }
}
