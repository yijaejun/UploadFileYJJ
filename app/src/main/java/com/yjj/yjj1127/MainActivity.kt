package com.yjj.yjj1127
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Surface
import android.webkit.JavascriptInterface
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






    }





    class JavascriptBridge(){
        @JavascriptInterface
        fun showMessage(){
            System.out.println("Message Received");
        }
    }






}