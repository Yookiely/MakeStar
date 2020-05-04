package com.example.yangzihang.makestar

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import com.example.yangzihang.makestar.network.UserImp
import kotlinx.android.synthetic.main.activity_agreement.*

class AgreementActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_agreement)
        val intent = intent
        val webView = findViewById<WebView>(R.id.agreement_webview)
        iv_agreement_back.setOnClickListener {
            onBackPressed()
        }
        webView.settings.apply {
            useWideViewPort =true
            loadWithOverviewMode =true
            textZoom =200
            builtInZoomControls =true
            displayZoomControls = false
        }
        val localLayoutParams = window.attributes
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags)
        val flag = intent.getStringExtra("FLAG")
        when(flag){
            "rule" ->{
                UserImp.getPrivateAgreement {
                    webView.loadData(it, "text/html", "utf-8")
                    webView.settings.javaScriptEnabled =true
                }
            }
            "agreement" -> {
                UserImp.getUserAgreement {
                    webView.loadData(it, "text/html", "utf-8")
                    webView.settings.javaScriptEnabled =true
                }
            }
            "about" -> {

            }
        }
    }
}
