package com.example.yangzihang.makestar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Window
import android.webkit.WebView
import com.example.yangzihang.makestar.network.UserImp

class AgreementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_agreement)
        val intent = intent
        val webView = findViewById<WebView>(R.id.agreement_webview)
        val flag = intent.getStringExtra("FLAG")
        when(flag){
            "rule" ->{
                UserImp.getPrivateAgreement {
                    webView.loadData(it, "text/html", "utf-8")
                }
            }
            "agreement" -> {
                UserImp.getUserAgreement {
                    webView.loadData(it, "text/html", "utf-8")
                }
            }
            "aboutus" -> {}
        }
    }
}
