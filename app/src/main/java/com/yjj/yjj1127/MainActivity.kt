package com.yjj.yjj1127

import android.Manifest
import android.R.attr.data
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
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
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            //Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
//            Toast.makeText(
//                this@MainActivity,
//                "Permission Denied\n$deniedPermissions",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    val url = "https://ps.uci.edu/~franklin/doc/file_upload.html";
    var mFilePatchCallback: ValueCallback<Array<Uri>>? = null

    var CARMER_PIC_ID = 20
    lateinit var filePath: String

    val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            var intent = result.data
            Log.e("[TAG]", result.resultCode.toString())
            Log.e("[TAG]", intent?.data.toString())

            if (result.resultCode == RESULT_OK) {
                if (intent == null) { // 카메라로 전송시 처리
                    intent = Intent()
                }
                if (intent.data == null) {
                    intent.data = photoUri
                }
                val results = intent.data!!
                mFilePatchCallback?.onReceiveValue(arrayOf(results))

            } else { // 취소 한 경우 초기화
                mFilePatchCallback?.onReceiveValue(null)
                mFilePatchCallback = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (VERSION_CODE > 28) {
            TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .check()
        } else {
            TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check()
        }

        val wv = binding.webView
        wv.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                try {
                    mFilePatchCallback = filePathCallback!!

                    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val StorageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", StorageDir)
                    photoUri = FileProvider.getUriForFile(
                        this@MainActivity,
                        "com.yjj.yjj1127.android.file-provider",
                        file
                    )

                    filePath = file.absolutePath

                    var takePictureIntent: Intent?
                    takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

//                    launcher.launch(takePictureIntent)

                    val contentSelectionIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    contentSelectionIntent.type = "image/*"

                    var intentArray: Array<Intent?>
                    intentArray = arrayOf(takePictureIntent)

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "사용할 앱을 선택해주세요.")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    launcher.launch(chooserIntent)

                    return true
                } catch (e: Exception) {
                    Log.d("[TAG]", e.toString())

                    return false
                }
            }
        }

        wv.loadUrl(url)

        binding.cameraButton.setOnClickListener {

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val StorageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", StorageDir)
            val photoUri: Uri =
                FileProvider.getUriForFile(this, "com.yjj.yjj1127.android.file-provider", file)

            filePath = file.absolutePath

            val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            //startActivityForResult(camera_intent, CARMER_PIC_ID);
            launcher.launch(camera_intent)
        }
    }

    // 카메라 기능 구현
    private fun runCamera(_isCapture: Boolean) {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val StorageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", StorageDir)
        //var photoUri : Uri =FileProvider.getUriForFile(this, "com.yjj.yjj1127.android.file-provider", file)

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //intentCamera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //File path = getFilesDir();
        // File 객체의 URI 를 얻는다.
        val photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "com.yjj.yjj1127.android.file-provider", file)
        } else {
            Uri.fromFile(file)
        }
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        if (!_isCapture) { // 선택팝업 카메라, 갤러리 둘다 띄우고 싶을 때
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            pickIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val pickTitle = "사진 가져올 방법을 선택하세요."
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)

            // 카메라 intent 포함시키기..
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(intentCamera))
            launcher.launch(chooserIntent)
        } else { // 바로 카메라 실행..
            launcher.launch(intentCamera)
        }
    }
}