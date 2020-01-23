package com.yookie.common.experimental.extensions

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.yookie.common.AuthService
import com.yookie.common.R
import com.yookie.common.WeiXinPay
import com.yookie.common.experimental.CommonContext
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream


object ShareMethod {
    //图片分享的限制大小
    const val IMAGE_SIZE = 32768

    fun showDialog(
        context: Context,
        workId: String,
        title: String,
        description: String
    ) {

        launch(UI + QuietCoroutineExceptionHandler) {
            val result = AuthService.getShareUrl(workId).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(context, "获取Url失败", Toast.LENGTH_SHORT).show()
            }

            if (result?.data == null || result.error_code != -1) {
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val items = arrayOf("分享到朋友圈", "分享到好友")
            val listDialog: AlertDialog.Builder = AlertDialog.Builder(context)
            val url = result.data.url
            listDialog.setTitle("分享到")
            listDialog.setItems(items) { _, which ->

                // which 下标从0开始

                when (which) {
                    0 -> weixinShare(context, url, title, description, true)
                    1 -> weixinShare(context, url, title, description, false)
                    else -> {
                    }
                }
            }
            listDialog.show()
        }

    }

    /**
     * 微信分享
     * 分享的图标为软件的icon
     *
     * @param url 网页链接
     * @param title 分享的标题
     * @param description 分享的内容描述
     * @param friendsCircle  是否分享到朋友圈 false表示发给好友；ture是朋友圈
     */
    fun weixinShare(
        context: Context,
        url: String,
        title: String,
        description: String,
        friendsCircle: Boolean
    ) {
        val wxAPI = WXAPIFactory.createWXAPI(context, CommonContext.WECHAT_APPID, true)
        wxAPI.registerApp(CommonContext.WECHAT_APPID)

        val webpage = WXWebpageObject()
        webpage.webpageUrl = url//分享url
        val msg = WXMediaMessage(webpage)
        msg.title = title
        msg.description = description
        msg.thumbData = getThumbData(context)

        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene =
            if (friendsCircle) SendMessageToWX.Req.WXSceneTimeline else SendMessageToWX.Req.WXSceneSession
        wxAPI!!.sendReq(req)
    }

    /**
     * 发起支付
     * @param weiXinPay
     */
    fun pay(context: Context, weiXinPay: WeiXinPay) {
        val wxAPI = WXAPIFactory.createWXAPI(context, CommonContext.WECHAT_APPID, true)
        wxAPI.registerApp(CommonContext.WECHAT_APPID)

        val req = PayReq()
        req.appId = CommonContext.WECHAT_APPID//appid
        req.nonceStr = weiXinPay.noncestr//随机字符串，不长于32位。推荐随机数生成算法
        req.packageValue = weiXinPay.package_value//暂填写固定值Sign=WXPay
        req.sign = weiXinPay.sign//签名
        req.partnerId = weiXinPay.partnerid//微信支付分配的商户号
        req.prepayId = weiXinPay.prepayid//微信返回的支付交易会话ID
        req.timeStamp = weiXinPay.timestamp//时间戳

        wxAPI?.registerApp(CommonContext.WECHAT_APPID)
        wxAPI.sendReq(req)
    }

    private fun getThumbData(context: Context): ByteArray? {
        val options = BitmapFactory.Options()
        options.inSampleSize = 2
        val bitmap = BitmapFactory.decodeResource(
            context.getResources(),
            R.drawable.ms_share_icon,
            options
        )
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        var quality = 100
        while (output.toByteArray().size > IMAGE_SIZE && quality != 10) {
            output.reset() // 清空baos
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                quality,
                output
            ) // 这里压缩options%，把压缩后的数据存放到baos中
            quality -= 10
        }
        bitmap.recycle()
        val result: ByteArray = output.toByteArray()
        try {
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}