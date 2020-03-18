package com.example.yangzihang.makestar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.example.yangzihang.makestar.View.EditTagInputItem
import com.example.yangzihang.makestar.View.editTagItem
import com.example.yangzihang.makestar.network.UserService
import com.example.yangzihang.makestar.utils.AppUtils
import com.hb.dialog.dialog.LoadingDialog
import com.yookie.common.AuthService
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_edit_detail.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.collections.forEachWithIndex

object EditType {
    val name: String = "name"
    val signature: String = "signature"
    val city: String = "city"
    val tag = "tags"
}

class EditDetailActivity : AppCompatActivity() {

    lateinit var loadingDialog: LoadingDialog
    private lateinit var recyclerView: RecyclerView
    private var itemManager: ItemManager = ItemManager()
    lateinit var editTagInputItem: EditTagInputItem
    var selectedTags = mutableListOf<String>()
    var totalSelected = CommonPreferences.tags.split(",").size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_edit_detail)
        iv_edit_detail_back.setOnClickListener { onBackPressed() }
        val bundle: Bundle? = intent.extras
        val editType = bundle?.getString(AppUtils.EDIT_INDEX)
        val mLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView = findViewById(R.id.rv_edit_detail_tags)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = ItemAdapter(itemManager)
        }
        loadingDialog = LoadingDialog(this)
        loadingDialog.setMessage("正在修改")

        if (CommonPreferences.tags == "") totalSelected--
        when (editType) {
            EditType.name -> {

            }
            EditType.signature -> {
                loadSignature()
            }
            EditType.city -> {
                loadCity()
            }
            EditType.tag -> {
                loadTags()
            }
        }

    }

    private fun loadSignature() {
        showEditText()
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
                    Toast.makeText(this@EditDetailActivity, result.message, Toast.LENGTH_SHORT)
                        .show()
                    loadingDialog.dismiss()
                    onBackPressed()
                }

                Toast.makeText(this@EditDetailActivity, result.message, Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
        }
    }

    private fun loadCity() {
        showEditText()
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
                    Toast.makeText(this@EditDetailActivity, result.message, Toast.LENGTH_SHORT)
                        .show()
                    loadingDialog.dismiss()
                    onBackPressed()
                }

                Toast.makeText(this@EditDetailActivity, result.message, Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
        }
    }

    private fun loadTags() {
        showTags()

        launch(UI + QuietCoroutineExceptionHandler) {
            val tags = UserService.getConstUserTags().awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@EditDetailActivity, "加载标签失败", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }?.data ?: return@launch

            var noNew = false
            val mTagList = CommonPreferences.tags.split(",")

            itemManager.refreshAll {
                tags.forEachWithIndex { _, constUserTag ->
                    editTagItem(
                        this@EditDetailActivity,
                        constUserTag.tag_name,
                        CommonPreferences.tags.contains(constUserTag.tag_name),
                        selectedTags
                    )
                    //1,2,3,4,5
                    //1,3       ==== true
                    //1,2,3,5,9 ==== false

                    noNew = noNew || (constUserTag.tag_name == mTagList.last())
                }

                editTagInputItem = if (noNew) EditTagInputItem(
                    this@EditDetailActivity,
                    "",
                    !noNew
                ) else EditTagInputItem(
                    this@EditDetailActivity,
                    mTagList.last(),
                    !noNew
                )

                add(editTagInputItem)
            }

            tv_edit_detail_confirm.setOnClickListener {
                launch(UI + QuietCoroutineExceptionHandler) {
                    loadingDialog.show()


                    val result =
                        AuthService.update(tags = getFormatTags()).awaitAndHandle {
                            it.printStackTrace()
                            Toast.makeText(this@EditDetailActivity, "修改失败", Toast.LENGTH_SHORT)
                                .show()
                            loadingDialog.dismiss()
                        } ?: return@launch

                    if (result.error_code == -1) {
                        CommonPreferences.tags = getFormatTags()
                        Toast.makeText(this@EditDetailActivity, result.message, Toast.LENGTH_SHORT)
                            .show()
                        loadingDialog.dismiss()
                        onBackPressed()
                    }

                    Toast.makeText(this@EditDetailActivity, result.message, Toast.LENGTH_SHORT)
                        .show()
                    loadingDialog.dismiss()
                }
            }
        }


    }

    private fun showEditText() {
        tv_edit_detail_count.visibility = View.GONE
        rv_edit_detail_tags.visibility = View.GONE
    }

    private fun showTags() {
        et_edit_detail.visibility = View.GONE
        tv_edit_detail_num.visibility = View.GONE
        refreshTagLabel()
    }

    private fun getFormatTags(): String {
        val tags = mutableListOf<String>()
        tags.apply {
            addAll(selectedTags)
            if (editTagInputItem.tagText != null) {
                add(editTagInputItem.tagText!!)
            }
            remove("")
        }
        var temp: String = ""
        tags.forEachWithIndex { i, s ->
            if (i != tags.size - 1) {
                temp += "$s,"
            } else {
                temp += s
            }
        }
        Log.d("momom", temp)
        return temp
    }

    fun refreshTagLabel() {
        tv_edit_detail_count.text = "已选标签(${totalSelected}/6)"
    }
}
