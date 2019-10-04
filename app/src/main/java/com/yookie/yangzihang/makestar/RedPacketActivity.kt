package com.yookie.yangzihang.makestar

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import com.yookie.yangzihang.makestar.network.UserService
import kotlinx.android.synthetic.main.activity_user_redpacket.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.textColor

class RedPacketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_user_redpacket)
        loadAndRefresh()

    }

    private fun loadAndRefresh() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val mRedPacket = UserService.getRedPacketInfo().awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@RedPacketActivity, "加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            Glide.with(this@RedPacketActivity).load(CommonPreferences.avatars)
                .error(R.drawable.ms_no_pic).into(cv_user_avatar)
            tv_user_name.text = CommonPreferences.username
            tv_user_money.text = "￥ ${mRedPacket.total_money}"
            tv_user_day.text = mRedPacket.red_bag_count.toString()
            tv_user_watch.text = mRedPacket.today_total_watch.toString()

            judgeState(tv_user_getmoney1, !mRedPacket.is_today_signed)
            judgeState(
                tv_user_getmoney2,
                (!mRedPacket.is_today_take_upload_money) && (mRedPacket.today_total_upload > 0)
            )
            judgeState(tv_user_getmoney3, !mRedPacket.is_today_take_watch_money)

            tv_user_getmoney1.setOnClickListener {
                if (!mRedPacket.is_today_signed) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val signMoney = UserService.signDay().awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(this@RedPacketActivity, "领取失败", Toast.LENGTH_SHORT)
                                .show()
                        } ?: return@launch

                        if (signMoney.error_code == -1 && signMoney.data != null) {
                            showDialogOfPic(signMoney.data!!.this_time_get_money)
                        }

                        loadAndRefresh()
                    }
                }
            }

            tv_user_getmoney2.setOnClickListener {
                if (!mRedPacket.is_today_take_watch_money) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val watchMoney = UserService.getWatchMoney().awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(this@RedPacketActivity, "领取失败", Toast.LENGTH_SHORT)
                                .show()
                        } ?: return@launch
                        if (watchMoney.error_code == -1 && watchMoney.data != null) {
                            showDialogOfPic(watchMoney.data!!.this_time_get_money)
                        }

                        loadAndRefresh()
                    }
                }
            }

            tv_user_getmoney3.setOnClickListener {
                if (!mRedPacket.is_today_take_upload_money) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val uploadMoney = UserService.getUploadMoney().awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(this@RedPacketActivity, "领取失败", Toast.LENGTH_SHORT)
                                .show()
                        } ?: return@launch

                        if (uploadMoney.error_code == -1 && uploadMoney.data != null) {
                            showDialogOfPic(uploadMoney.data!!.this_time_get_money)
                        }

                        loadAndRefresh()
                    }
                }
            }
        }
    }

    private fun judgeState(textView: TextView, isRed: Boolean) {
        textView.textColor = if (isRed) Color.parseColor("#E3294B") else Color.parseColor("#999999")
        textView.background = if (isRed) ContextCompat.getDrawable(
            this,
            R.drawable.ms_red_get
        ) else ContextCompat.getDrawable(this, R.drawable.ms_grey_get)
    }

    private fun showDialogOfPic(numOfMoney: String) {
        val dialog = Dialog(this, R.style.edit_AlertDialog_style)
        dialog.apply {
            setContentView(R.layout.dialog_user_redpacket)
            val money = this.findViewById<TextView>(R.id.tv_dialog_money)
            val background = this.findViewById<ImageView>(R.id.iv_dialog_background)
            money.text = "￥${numOfMoney}"

            setCanceledOnTouchOutside(true)
            val window = window
            val lp = window.attributes
            lp.x = 4
            lp.y = 4
            dialog.onWindowAttributesChanged(lp)
            background.setOnClickListener { dismiss() }
            show()
        }

    }
}
