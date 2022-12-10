package com.yjj.yjj1127
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Surface
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


        val wv=binding.webView
        wv.settings.javaScriptEnabled=true


        wv.addJavascriptInterface(
            JavascriptBridge(),
            "Native"
        )


        wv.loadUrl("file:///android_asset/webview1.html")

//        Surface(color = MaterialTheme.colors.background) {
//            Column {
//                AndroidView(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(300.dp),
//                    factory = { context ->
//                        WebView(context).apply {
//                            settings.javaScriptEnabled = true
//                            setBackgroundColor(Color.TRANSPARENT)
//                            loadUrl("file:///android_asset/webview1.html")
//                            addJavascriptInterface(
//                                JavascriptBridge(),
//                                "Native"
//                            )
//                        }
//                    }
//                )
//            }
//        }
    }


    class JavascriptBridge(){
        @JavascriptInterface
        fun showMessage(){
            System.out.println("Message Received");
            Toast.makeText(this@MainActivity, "Message Received", Toast.LENGTH_SHORT).show()
        }
    }






}