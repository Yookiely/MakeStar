package com.yookie.yangzihang.makestar

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Window
import com.aliyun.svideo.snap.SnapCropSetting.PERMISSION_REQUEST_CODE
import com.wingedvampires.homepage.view.HomePageActivity
import com.yookie.auth.LoginActivity
import com.yookie.common.experimental.preference.CommonPreferences
import com.yookie.yangzihang.makestar.extensions.AppArchmage
import com.yookie.yangzihang.makestar.utils.PermissionUtils
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    internal var permission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        AppArchmage.init()
        val checkResult = PermissionUtils.checkPermissionsGroup(this, permission)
        if (!checkResult) {
            PermissionUtils.requestPermissions(this, permission, PERMISSION_REQUEST_CODE)
        }
        if (CommonPreferences.isLogin) {
            startActivity<HomePageActivity>()
            finish()
        } else startActivity<LoginActivity>()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var isAllGranted = true

            // 判断是否所有的权限都已经授予了
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false
                    break
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了
                //Toast.makeText(this, "get All Permisison", Toast.LENGTH_SHORT).show();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                showPermissionDialog()
            }
        }
    }

    //系统授权设置的弹框
    internal var openAppDetDialog: AlertDialog? = null

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.app_name) + "需要访问 \"相册\"、\"摄像头\" 和 \"外部存储器\",否则会影响绝大部分功能使用, 请到 \"应用信息 -> 权限\" 中设置！")
        builder.setPositiveButton("去设置") { dialog, which ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
        }
        builder.setCancelable(false)
        builder.setNegativeButton("暂不设置") { dialog, which ->
            //finish();
        }
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create()
        }
        if (null != openAppDetDialog && !openAppDetDialog!!.isShowing) {
            openAppDetDialog!!.show()
        }
    }
}
