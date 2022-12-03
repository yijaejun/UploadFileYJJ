package com.yjj.yjj1127

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.yjj.yjj1127.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val url="https://ps.uci.edu/~franklin/doc/file_upload.html";
    var mFilePatchCallback: ValueCallback<Array<Uri>>?=null

    lateinit var currentPhotoPath: String
    var CARMER_PIC_ID=20
    lateinit var filePath: String

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



        binding.cameraButton.setOnClickListener {

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val StorageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", StorageDir)
            val photoUri : Uri = FileProvider.getUriForFile(this, "com.yjj.yjj1127.android.file-provider", file)

            filePath = file.absolutePath

            val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(camera_intent, CARMER_PIC_ID);



        }


    }


    // This method will help to retrieve the image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Match the request 'pic id with requestCode
        if (requestCode == CARMER_PIC_ID && resultCode == Activity.RESULT_OK) {




        }
    }


//
//    // PICK_PHOTO_CODE is a constant integer
//    val PICK_PHOTO_CODE = 1046
//
//    // Trigger gallery selection for a photo
//    fun onPickPhoto(view: View?) {
//        // Create intent for picking a photo from the gallery
//        val intent = Intent(
//            Intent.ACTION_PICK,
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        )
//
//        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
//        // So as long as the result is not null, it's safe to use the intent.
//        if (intent.resolveActivity(packageManager) != null) {
//            // Bring up gallery to select a photo
//            startActivityForResult(intent, PICK_PHOTO_CODE)
//        }
//    }
//
//    fun loadFromUri(photoUri: Uri?): Bitmap? {
//        var image: Bitmap? = null
//        try {
//            // check version of Android on device
//            image = if (Build.VERSION.SDK_INT > 27) {
//                // on newer versions of Android, use the new decodeBitmap method
//                val source: ImageDecoder.Source =
//                    ImageDecoder.createSource(this.contentResolver, photoUri)
//                ImageDecoder.decodeBitmap(source)
//            } else {
//                // support older versions of Android by using getBitmap
//                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return image
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (data != null && requestCode == PICK_PHOTO_CODE) {
//            val photoUri = data.data
//
//            // Load the image located at photoUri into selectedImage
//            val selectedImage = loadFromUri(photoUri)
//
//            // Load the selected image into a preview
//            val ivPreview: ImageView = findViewById<View>(R.id.ivPreview) as ImageView
//            ivPreview.setImageBitmap(selectedImage)
//        }
//    }


//    val APP_TAG = "MyCustomApp"
//    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
//    val photoFileName = "photo.jpg"
//    var photoFile: File? = null
//
//    fun onLaunchCamera() {
//        // create Intent to take a picture and return control to the calling application
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        // Create a File reference for future access
//        photoFile = getPhotoFileUri(photoFileName)
//
//        // wrap File object into a content provider
//        // required for API >= 24
//        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
//        if (photoFile != null) {
//            val fileProvider: Uri =
//                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
//
//            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
//            // So as long as the result is not null, it's safe to use the intent.
//
//            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
//            // So as long as the result is not null, it's safe to use the intent.
//            if (intent.resolveActivity(packageManager) != null) {
//                // Start the image capture intent to take photo
//                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
//            }
//        }
//    }
//
//    // Returns the File for a photo stored on disk given the fileName
//    fun getPhotoFileUri(fileName: String): File? {
//        // Get safe storage directory for photos
//        // Use `getExternalFilesDir` on Context to access package-specific directories.
//        // This way, we don't need to request external read/write runtime permissions.
//        val mediaStorageDir =
//            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
//            Log.d(APP_TAG, "failed to create directory")
//        }
//
//        // Return the file target for the photo based on filename
//        return File(mediaStorageDir.path + File.separator + fileName)
//    }
//


//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//    }

//    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    ...
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this,
//                        "com.example.android.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                }
//            }
//        }
//    }



}