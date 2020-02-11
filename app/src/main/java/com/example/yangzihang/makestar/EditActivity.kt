package com.example.yangzihang.makestar

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.example.yangzihang.makestar.View.EditPicItem
import com.example.yangzihang.makestar.View.editInfoItem
import com.example.yangzihang.makestar.View.editPicItem
import com.example.yangzihang.makestar.utils.AppUtils
import com.hb.dialog.dialog.LoadingDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.yookie.common.AuthService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.startActivity
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager = ItemManager()
    private lateinit var pvTime: TimePickerView
    private lateinit var pvOptions: OptionsPickerView<String>
    private lateinit var editPicItem: EditPicItem
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_edit)
        iv_edit_back.setOnClickListener { onBackPressed() }

        loadingDialog = LoadingDialog(this)
        loadingDialog.setMessage("正在修改")
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rv_user_edit)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }

        initTimePicker()
        initOptionPicker()
        loadInfo()
    }


    override fun onResume() {
        super.onResume()
        loadInfo()
    }

    private fun loadInfo() = itemManager.refreshAll {
        clear()
        editPicItem("头像", this@EditActivity, CommonPreferences.avatars) {
            editPicItem = it
            checkPermAndOpenPic()
        }

        editInfoItem("名称", CommonPreferences.username, false) {
            it.context.startActivity<EditDetailActivity>(AppUtils.EDIT_INDEX to EditType.name)
        }

        editInfoItem("个性签名", CommonPreferences.signature, true) {
            it.context.startActivity<EditDetailActivity>(AppUtils.EDIT_INDEX to EditType.signature)
        }

        editInfoItem("地区", CommonPreferences.city, true) {
            it.context.startActivity<EditDetailActivity>(AppUtils.EDIT_INDEX to EditType.city)
        }

        editInfoItem("性别", CommonPreferences.sex, true) {
            pvOptions.show(it)
        }

        editInfoItem("生日", CommonPreferences.birthday, true) {
            pvTime.show(it)
        }

        editInfoItem("标签", "", true) {
            it.context.startActivity<EditDetailActivity>(AppUtils.EDIT_INDEX to EditType.tag)
        }
    }


    private fun initTimePicker() { //Dialog 模式下，在底部弹出
        pvTime = TimePickerBuilder(this,
            OnTimeSelectListener { date, v ->
                val birthday = getTime(date)

                if (birthday != null) {
                    uploadAndRefreshBirthday(birthday) {
                        CommonPreferences.birthday = birthday
                    }
                }
            })
            .setTimeSelectChangeListener { Log.i("pvTime", "onTimeSelectChanged") }
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .setCancelColor(Color.BLACK)
            .setSubmitColor(Color.BLACK)
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .addOnCancelClickListener {
                Log.i(
                    "pvTime",
                    "onCancelClickListener"
                )
            }
            .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .build()
        val mDialog: Dialog = pvTime.getDialog()
        if (mDialog != null) {
            val params =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
                )
            params.leftMargin = 0
            params.rightMargin = 0
            pvTime.dialogContainerLayout.layoutParams = params
            val dialogWindow = mDialog.window
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim) //修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f)
            }
        }
    }

    private fun initOptionPicker() { //条件选择器初始化
        val list = mutableListOf<String>()
        list.apply {
            add("男")
            add("女")
        }

        pvOptions = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, _, _, v ->
                //返回的分别是三个级别的选中位置
                Log.d("momomom", list[options1])
                uploadAndRefreshSex(options1 + 1) {
                    CommonPreferences.sex = list[options1]
                }
            })
            .setContentTextSize(20) //设置滚轮文字大小
            .setDividerColor(Color.LTGRAY) //设置分割线的颜色
            .setSelectOptions(0, 1) //默认选中项
            .setCancelColor(Color.BLACK)
            .setSubmitColor(Color.BLACK)
            .setTextColorCenter(Color.LTGRAY)
            .isRestoreItem(true) //切换时是否还原，设置默认选中第一项。
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setOutSideColor(0x00000000) //设置外部遮罩颜色
            .build()
        //        pvOptions.setSelectOptions(1,1);
        pvOptions.setPicker(list) //一级选择器

        when (CommonPreferences.sex) {
            "男" -> pvOptions.setSelectOptions(0)
            "女" -> pvOptions.setSelectOptions(1)
        }
    }

    private fun getTime(date: Date): String? { //可根据需要自行截取数据显示
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    private fun openSelectPic() = PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
        .maxSelectNum(9)// 最大图片选择数量 int
        .minSelectNum(1)// 最小选择数量 int
        .imageSpanCount(4)// 每行显示个数 int
        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
        .previewImage(true)// 是否可预览图片 true or false
        .isCamera(true)// 是否显示拍照按钮 true or false
        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
        .forResult(PictureConfig.CHOOSE_REQUEST)

    private fun checkPermAndOpenPic() {
        // 检查存储权限
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            EasyPermissions.requestPermissions(
                this,
                "需要外部存储来提供必要的缓存",
                0,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            openSelectPic()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    Log.d("whatthefuck", selectList[0].path)
                    editPicItem.loadAnUploadPic(selectList[0].path) {
                        CommonPreferences.avatars = selectList[0].path
                        loadInfo()
                    }

                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                }

            }
        }
    }

    private fun uploadAndRefreshSex(sex: Int, block: () -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            loadingDialog.show()
            val result =
                AuthService.update(sex = sex).awaitAndHandle {
                    it.printStackTrace()
                    Toast.makeText(this@EditActivity, "修改失败", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                } ?: return@launch

            if (result.error_code == -1) {
                block()
            }

            Toast.makeText(this@EditActivity, result.message, Toast.LENGTH_SHORT).show()
            loadInfo()
            loadingDialog.dismiss()
        }
    }

    private fun uploadAndRefreshBirthday(birthday: String, block: () -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            loadingDialog.show()
            val result =
                AuthService.update(age = birthday).awaitAndHandle {
                    it.printStackTrace()
                    Toast.makeText(this@EditActivity, "修改失败", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                } ?: return@launch

            if (result.error_code == -1) {
                block()
            }

            Toast.makeText(this@EditActivity, result.message, Toast.LENGTH_SHORT).show()
            loadInfo()
            loadingDialog.dismiss()
        }
    }

}
