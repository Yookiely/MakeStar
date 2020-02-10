package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.Toast
import com.example.yangzihang.makestar.utils.AppUtils
import com.hb.dialog.dialog.LoadingDialog
import com.yookie.common.AuthService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_edit_detail.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

object EditType {
    val name: String = "name"
    val signature: String = "signature"
    val city: String = "city"
}

class EditDetailActivity : AppCompatActivity() {

    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_edit_detail)
        iv_edit_detail_back.setOnClickListener { onBackPressed() }
        val bundle: Bundle? = intent.extras
        val editType = bundle?.getString(AppUtils.EDIT_INDEX)

        loadingDialog = LoadingDialog(this)
        loadingDialog.setMessage("正在修改")

        when (editType) {
            EditType.name -> {

            }
            EditType.signature -> {
                loadSignature()
            }
            EditType.city -> {
                loadCity()
            }
        }

    }


    private fun loadSignature() {
        et_edit_detail.setText(CommonPreferences.signature)

        tv_edit_detail_confirm.setOnClickListener {
            launch(UI + QuietCoroutineExceptionHandler) {
                loadingDialog.show()
                val result =
                    AuthService.update(signature = et_edit_detail.text.toString()).awaitAndHandle {
                        it.printStackTrace()
                        Toast.makeText(this@EditDetailActivity, "修改失败", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    } ?: return@launch

                if (result.error_code == -1) {
                    CommonPreferences.signature = et_edit_detail.text.toString()
                }

                Toast.makeText(this@EditDetailActivity, result.message, Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
        }
    }

    private fun loadCity() {
        et_edit_detail.setText(CommonPreferences.city)

        tv_edit_detail_confirm.setOnClickListener {
            launch(UI + QuietCoroutineExceptionHandler) {
                loadingDialog.show()
                val result =
                    AuthService.update(city_Name = et_edit_detail.text.toString()).awaitAndHandle {
                        it.printStackTrace()
                        Toast.makeText(this@EditDetailActivity, "修改失败", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    } ?: return@launch

                if (result.error_code == -1) {
                    CommonPreferences.city = et_edit_detail.text.toString()
                }

                Toast.makeText(this@EditDetailActivity, result.message, Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
        }
    }
}
