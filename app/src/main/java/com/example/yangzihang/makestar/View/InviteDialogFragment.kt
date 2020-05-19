package com.example.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.RedPacketActivity
import com.example.yangzihang.makestar.network.UserService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class InviteDialogFragment() : DialogFragment() {
    var redPacketActivity: RedPacketActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.edit_AlertDialog_style)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        val view: View = inflater.inflate(
            com.example.yangzihang.makestar.R.layout.dialog_user_redpacket_input,
            container
        )

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        val num =
            view.findViewById<EditText>(R.id.tv_redpacketinput_num)
        val confirm =
            view.findViewById<TextView>(R.id.tv_redpacketinput_confirm)
        num.clearFocus()
        confirm.setOnClickListener {
            redPacketActivity?.hideSoftInputMethod()
            num.isFocusable = false
            num.isFocusableInTouchMode = true

            if (num.text.length == 12 && num.text.isNotBlank())
                launch(UI + QuietCoroutineExceptionHandler) {
                    val result = UserService.useInvitationCode(num.text.toString()).awaitAndHandle {
                        it.printStackTrace()
                        Toast.makeText(redPacketActivity, "邀请失败", Toast.LENGTH_SHORT).show()
                    } ?: return@launch

                    if (result.error_code == 2) {
                        Toast.makeText(redPacketActivity, "您不是新用户", Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    } else if (result.error_code != -1) {
                        Toast.makeText(redPacketActivity, "邀请码过期或有误", Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    } else {
                        Toast.makeText(redPacketActivity, "邀请码使用成功", Toast.LENGTH_SHORT)
                            .show()
                    }

                    redPacketActivity?.loadAndRefresh()
                }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val window = this.dialog.window
        val lp = window.attributes
        window.decorView.setPadding(0, 0, 0, 20);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.setLayout(-1, -2) //高度自适应，宽度全屏
        lp.gravity = Gravity.TOP

        window.attributes = lp;
    }
}