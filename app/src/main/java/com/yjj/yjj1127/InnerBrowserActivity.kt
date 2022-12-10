package com.yjj.yjj1127

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.yjj.yjj1127.databinding.ActivityInnerBrowserBinding

class InnerBrowserActivity : AppCompatActivity() {

    private val binding: ActivityInnerBrowserBinding by lazy { ActivityInnerBrowserBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Toast.makeText(this, "${intent.getStringExtra("URL")}", Toast.LENGTH_SHORT).show()
        val url = intent.getStringExtra("URL")


        val wv=binding.webView

        wv.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                //return super.shouldOverrideUrlLoading(view, request)
                return false // 외부 브라우저를 사용하지 못하도록 처리됨.
            }
        }

        wv.loadUrl(url.toString())

    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }

}