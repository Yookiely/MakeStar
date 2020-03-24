package com.example.yangzihang.makestar

import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.View.MyCircleItem
import com.example.yangzihang.makestar.network.UserImp
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

class FansHomeActivity : AppCompatActivity() {
    lateinit var rec :RecyclerView
    lateinit var myRec :RecyclerView
    lateinit var myCircle : TextView
    lateinit var search: EditText
    lateinit var back :ImageView
    private val QQAPPID  = "101831652"
    lateinit var context:Context
    var items = mutableListOf<Item>()
    var myItems = mutableListOf<Item>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_fans_home)
        myRec =findViewById(R.id.fans_home_myrec)
        context =this
        myCircle =findViewById(R.id.fans_home_mycircle)
        rec =findViewById(R.id.fans_home_jionrec)
        search =findViewById(R.id.fans_home_search)
        search.focusable = View.NOT_FOCUSABLE
        back = findViewById(R.id.fans_home_back)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        myRec.layoutManager =LinearLayoutManager(this)
        rec.layoutManager =LinearLayoutManager(this)
        UserImp.getFandomList { fandomList ->
            if(fandomList.my.isEmpty()){
                myCircle.visibility = View.INVISIBLE
            }else{
                fandomList.my.forEach {
                    myItems.add(MyCircleItem(it.host_avatar,it.host_username,it.host_user_ID,this))
                }
                myRec.withItems(myItems)
            }
            fandomList.others.forEach {
                items.add(MyCircleItem(it.host_avatar,it.host_username,it.host_user_ID,this))
            }
            rec.withItems(items)
        }
        back.setOnClickListener {
            qqLogin()
        }
    }
    private fun qqLogin(){
        val mTencent = Tencent.createInstance(QQAPPID,this.applicationContext)
        if (!mTencent.isSessionValid){
            mTencent.login(this, "all", object : IUiListener {
                override fun onComplete(p0: Any?) {
                    Toast.makeText(context,"授权成功", Toast.LENGTH_SHORT).show()
                }

                override fun onCancel() {
                }

                override fun onError(p0: UiError?) {
                }

            } )
        }

    }
}
