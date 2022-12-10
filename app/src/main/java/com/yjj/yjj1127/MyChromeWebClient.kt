package com.yjj.yjj1127

import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.WebView

class MyChromeWebClient: WebChromeClient() {
    var mFilePatchCallback: ValueCallback<Array<Uri>>?=null
    var cameraPath = ""


    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {

        if(mFilePatchCallback !== null){
            mFilePatchCallback?.onReceiveValue(null)
            mFilePatchCallback=null
        }

        mFilePatchCallback=filePathCallback

        var takePictureIntent : Intent?
        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)



        val contentSelectionIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        contentSelectionIntent.type = "image/*"

        var intentArray: Array<Intent?>

        if(takePictureIntent != null) intentArray = arrayOf(takePictureIntent)
        else intentArray = takePictureIntent?.get(0)!!

        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE,"사용할 앱을 선택해주세요.")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        //launcher.launch(chooserIntent)


        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }




}