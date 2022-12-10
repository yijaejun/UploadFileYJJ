package com.yjj.yjj1127
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.yjj.yjj1127.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getToken()


        val wv=binding.webView
        wv.settings.javaScriptEnabled=true
        //wv.webChromeClient = MyChromeWebClient()

        wv.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                //return super.shouldOverrideUrlLoading(view, request)
                return false // 외부 브라우저를 사용하지 못하도록 처리됨.
            }
        }


        wv.addJavascriptInterface(
            JavascriptBridge(),
            "Native"
        )


        wv.loadUrl("file:///android_asset/webview1.html")
    }


    private fun getToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TOKEN", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            //val msg = getString(R.string.msg_token_fmt, token)
            Log.e("TOKEN", token)
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })


    }

    override fun onBackPressed() {
        // super.onBackPressed()
        // binding.webView.loadUrl("javascript: onBackClick()")

    }

    inner class JavascriptBridge(){
        @JavascriptInterface
        fun showMessage(){
            System.out.println("Message Received")
            Toast.makeText(this@MainActivity, "Message Received", Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun appClose(){
            Toast.makeText(this@MainActivity, "APP CLOSE", Toast.LENGTH_SHORT).show()
            finish()
        }

        @JavascriptInterface
        fun showOutBrowser(doOut: Boolean, url : String){
            Log.e("[TAG]", doOut.toString())
            Log.e("[TAG]", url)

            if(doOut){
                val intent = Intent(this@MainActivity, InnerBrowserActivity::class.java)
                intent.putExtra("URL", url)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }else{
                val intent = Intent(this@MainActivity, OuterBrowserActivity::class.java)
                intent.putExtra("URL", url)
                startActivity(intent)
            }

            //binding.webView.loadUrl(url)

//            if(doOut){
//                binding.webView.webViewClient= object : WebViewClient(){
//                    override fun shouldOverrideUrlLoading(
//                        view: WebView?,
//                        request: WebResourceRequest?
//                    ): Boolean {
//                        //return super.shouldOverrideUrlLoading(view, request)
//                        return true
//                    }
//                }
//            }else{
//                binding.webView.webViewClient= object : WebViewClient(){
//                    override fun shouldOverrideUrlLoading(
//                        view: WebView?,
//                        request: WebResourceRequest?
//                    ): Boolean {
//                        //return super.shouldOverrideUrlLoading(view, request)
//                        return false
//                    }
//                }
//            }
//
//
//            binding.webView.loadUrl(url)
        }

//        @JavascriptInterface
//        fun showOutBrowser(url: String){
//            binding.webView.webViewClient= object : WebViewClient(){
//                override fun shouldOverrideUrlLoading(
//                    view: WebView?,
//                    request: WebResourceRequest?
//                ): Boolean {
//                    //return super.shouldOverrideUrlLoading(view, request)
//                    return true
//                }
//            }
//
//            binding.webView.loadUrl(url)
//
//        }


    }




    // TODO 두 번 탭하여 앱을 종료시키기


    // TODO 네이버로 이동하고나서 다시 뒤로 돌아가기가 안된다. 이 때는 자바스크립트로 제어가 안되는데 이때는 어떻게 해야 하는가?


    // TODO 액티비티를 하나 더 만들어서 외부 URL을 띄우고 백버튼으로 닫을 수 있도록 해보자.









}