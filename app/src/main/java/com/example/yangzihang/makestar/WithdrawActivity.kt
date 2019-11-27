package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.Toast
import com.example.yangzihang.makestar.network.UserService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import kotlinx.android.synthetic.main.activity_withdraw.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class WithdrawActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_withdraw)

        iv_withdraw_back.setOnClickListener { onBackPressed() }

        launch(UI + QuietCoroutineExceptionHandler) {
            val mRedPacket = UserService.getRedPacketInfo().awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@WithdrawActivity, "加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            tv_withdraw_total_money.text = mRedPacket.total_money
            tv_withdraw_selectcard.setOnClickListener {
                Toast.makeText(this@WithdrawActivity, "您的余额未达到最低限额，不能添加银行卡", Toast.LENGTH_SHORT)
                    .show()
            }

            tv_withdraw_getall.setOnClickListener {
                et_withdraw_money.setText(mRedPacket.total_money)
            }

            tv_withdraw_get.setOnClickListener {
                et_withdraw_money.clearFocus()
                if (et_withdraw_money.text.toString().toFloat() > mRedPacket.total_money.toFloat()) {
                    Toast.makeText(this@WithdrawActivity, "超出余额", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@WithdrawActivity, "未达到最低限额", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
