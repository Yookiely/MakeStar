package com.yookie.yangzihang.makestar

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.PopupWindow
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
            judgeState(
                tv_user_getmoney3,
                (!mRedPacket.is_today_take_watch_money) && (mRedPacket.today_total_watch >= 5)
            )

            tv_user_getmoney1.setOnClickListener {
                if (!mRedPacket.is_today_signed) {
                    launch(UI + QuietCoroutineExceptionHandler) {
                        val signMoney = UserService.signDay().awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(
                                this@RedPacketActivity,
                                "领取失败",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } ?: return@launch

                        if (signMoney.error_code == -1 && signMoney.data != null) {
                            showDialogOfPic(signMoney.data!!.this_time_get_money)
                        }

                        loadAndRefresh()
                    }
                }

            }

            tv_user_getmoney3.setOnClickListener {
                if ((!mRedPacket.is_today_take_watch_money) && (mRedPacket.today_total_watch >= 5)) {
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

            tv_user_getmoney2.setOnClickListener {
                if ((!mRedPacket.is_today_take_upload_money) && (mRedPacket.today_total_upload > 0)) {
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
        val popupWindowView =
            LayoutInflater.from(this).inflate(R.layout.dialog_user_redpacket, null, false)
        val money = popupWindowView.findViewById<TextView>(R.id.tv_dialog_money)
        money.text = "￥${numOfMoney}"
        val popupWindow = PopupWindow(
            popupWindowView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )
        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            isTouchable = true
//            Glide.with(this)
//                .load(item.images[position])
//                .error(R.drawable.lf_detail_np)
//                .into(popupWindowView.findViewById(R.id.iv_detail_popupwindow))


            setBackgroundDrawable(BitmapDrawable())
//            popupWindow.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(item.detailActivity, R.color.white_color)))
            bgAlpha(0.5f)
            showAsDropDown(tb_user_redpacket, Gravity.CENTER, 0, 0)
            setOnDismissListener {
                // popupWindow 隐藏时恢复屏幕正常透明度
                bgAlpha(1f)
            }
        }
        popupWindowView.setOnClickListener { popupWindow.dismiss() }


    }

    private fun bgAlpha(bgAlpha: Float) {
        val lp = window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
    }
}
