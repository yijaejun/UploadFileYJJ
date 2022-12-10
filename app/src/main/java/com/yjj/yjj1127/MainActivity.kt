package com.yjj.yjj1127

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.yjj.yjj1127.BuildConfig.VERSION_CODE
import com.yjj.yjj1127.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private var photoUri: Uri? = null
    val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }




    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            // Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
//            Toast.makeText(
//                this@MainActivity,
//                "Permission Denied\n$deniedPermissions",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    val url="https://ps.uci.edu/~franklin/doc/file_upload.html";
    var mFilePatchCallback: ValueCallback<Array<Uri>>?=null

    var CARMER_PIC_ID=20
    lateinit var filePath: String
    private var MINIMUM_SDK_VERSION = 28

    // onStart or onCreate에서 작동될 수 있도록
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        var intent = result.data
        Log.d("[TAG]", result.resultCode.toString())
        Log.d("[TAG]", intent?.data.toString())

        if (result.resultCode == RESULT_OK) {
            if(intent == null){
                intent = Intent()
            }

            if(intent.data == null){ // 카메라 처리
                Log.d("[TAG2]", filePath)
                intent.data=photoUri
            }

            val results = intent?.data!!
            mFilePatchCallback?.onReceiveValue(arrayOf(results))

        } else{ // 취소 한 경우 초기화
            mFilePatchCallback?.onReceiveValue(null)
            mFilePatchCallback = null
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(VERSION_CODE <= MINIMUM_SDK_VERSION){
            TedPermission.create()
                .setPermissionListener(permissionlistener)
//                .setRationaleMessage("카메라 권한이 필요합니다.")
//                .setDeniedMessage("카메라 권한을 거부하셨습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
        }else{
            TedPermission.create()
                .setPermissionListener(permissionlistener)
//                .setRationaleMessage("카메라 권한이 필요합니다.")
//                .setDeniedMessage("카메라 권한을 거부하셨습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
        }


        val wv=binding.webView

        wv.settings.javaScriptEnabled=true


        wv.webChromeClient= object: WebChromeClient(){
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                try{
                    mFilePatchCallback = filePathCallback!!


                    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val StorageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", StorageDir)

                    photoUri = FileProvider.getUriForFile(this@MainActivity, "com.yjj.yjj1127.android.file-provider", file)
                    filePath = file.absolutePath


                    var takePictureIntent : Intent?
                    takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)


                    // Log.d("[TAG2]", takePictureIntent.toString())

                    //launcher.launch(takePictureIntent)

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



    class JavascriptBridge(){
        @JavascriptInterface
        fun showMessage(){
            System.out.println("Message Received");
        }
    }






}