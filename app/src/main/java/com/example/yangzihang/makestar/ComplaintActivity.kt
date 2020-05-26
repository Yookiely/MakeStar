package com.example.yangzihang.makestar

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.yangzihang.makestar.network.UserService
import com.hb.dialog.dialog.LoadingDialog
import com.yookie.common.experimental.extensions.ComplaintType
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import kotlinx.android.synthetic.main.activity_complaint.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ComplaintActivity : AppCompatActivity() {

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_complaint)

        val localLayoutParams = window.attributes
        localLayoutParams.flags =
            (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)

        iv_complaint_back.setOnClickListener {
            onBackPressed()
        }
        loadingDialog = LoadingDialog(this)
        loadingDialog.setMessage("正在上传")
        loadingDialog.setCanceledOnTouchOutside(false)
        val complaintType = intent.getIntExtra(ComplaintType.COMPLAINT_TYPE, -1)
        val complaintId = intent.getStringExtra(ComplaintType.COMPLAINT_ID)

        if (complaintType == -1 || complaintId == null) {
            this.finish()
        }

        tv_complaint_confirm.setOnClickListener { v ->
            hideSoftInputMethod()
            loadingDialog.show()
            v.isEnabled = false
            val reason = et_complaint_detail.text.toString()
            launch(UI + QuietCoroutineExceptionHandler) {
                if (reason.isNotBlank()) {
                    val result =
                        UserService.sendComplaint(complaintType, complaintId.toString(), reason)
                            .awaitAndHandle {
                                it.printStackTrace()
                                v.isEnabled = true
                                loadingDialog.dismiss()
                                Toast.makeText(this@ComplaintActivity, "发送失败哦", Toast.LENGTH_SHORT)
                                    .show()
                            }

                    if (result?.error_code == -1) {
                        loadingDialog.dismiss()
                        Toast.makeText(this@ComplaintActivity, "投诉成功", Toast.LENGTH_SHORT).show()
                        this@ComplaintActivity.finish()
                    }

                    loadingDialog.dismiss()
                    v.isEnabled = true
                    Toast.makeText(this@ComplaintActivity, result?.message, Toast.LENGTH_SHORT)
                        .show()

                } else {
                    loadingDialog.dismiss()
                    v.isEnabled = true
                    Toast.makeText(this@ComplaintActivity, "请正确填写投诉内容哦", Toast.LENGTH_SHORT).show()
                }

                v.isEnabled = true
            }
        }
    }

    private fun hideSoftInputMethod() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.apply {
            hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
    }

    private fun showExitDialog() = AlertDialog.Builder(this)
        .setTitle("放弃编辑吗～")
        .setCancelable(false)
        .setPositiveButton("取消") { dialog, _ -> dialog.dismiss() }
        .setNegativeButton("确定") { _, _ -> this.finish() }
        .create()
        .show()

    override fun onBackPressed() = showExitDialog()
}
