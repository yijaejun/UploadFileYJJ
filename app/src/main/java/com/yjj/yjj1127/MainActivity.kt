package com.yjj.yjj1127

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import com.yjj.yjj1127.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val url="https://ps.uci.edu/~franklin/doc/file_upload.html";
    var mFilePatchCallback: ValueCallback<Array<Uri>>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data

                Log.d("[TAG]", intent.toString())
                Log.d("[TAG]", intent!!.data.toString())

                if(intent.equals(null)){ // 카메라로 전송시 처리



                } else{ // 갤러리 선택
                    val results = intent.data!!
                    mFilePatchCallback?.onReceiveValue(arrayOf(results))
                }
            } else{ // 취소 한 경우 초기화
                mFilePatchCallback?.onReceiveValue(null)
                mFilePatchCallback = null
            }
        }


        val wv=binding.webView
        wv.webChromeClient= object: WebChromeClient(){
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                try{
                    mFilePatchCallback = filePathCallback!!

                    var takePictureIntent : Intent?
                    takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)



                    val contentSelectionIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    contentSelectionIntent.type = "image/*"

                    var intentArray: Array<Intent?>
                    intentArray = arrayOf(takePictureIntent)

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE,"사용할 앱을 선택해주세요.")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    launcher.launch(chooserIntent)

                    return true
                }catch (e: Exception){
                    Log.d("[TAG]", e.toString())

                    return false
                }
            }
        }

        wv.loadUrl(url)

    }



}