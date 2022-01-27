package com.iunga.imagedetectionprototype1

import android.R.attr
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap


class MainActivity : AppCompatActivity() {



    private val cameraButton: Button by lazy {
        findViewById(R.id.cameraButton)
    }
    private val imageView: ImageView by lazy {
        findViewById(R.id.imageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCameraButton()

    }

    private fun initCameraButton() {
        cameraButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigateCamera()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
                }
            }
        }
    }


    private fun navigateCamera() {
        //TODO 촬영 후 이미지 뷰에 이미지
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    val REQUEST_IMAGE_CAPTURE = 1

    //TODO 이미지 생성 URL 변수에 넣고 이 URL 변수로
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.getParcelableExtra<Bitmap>("data")
            Log.d("imageUri:  ", imageBitmap.toString())
            imageView.setImageBitmap(imageBitmap)
        }
    }

    //TODO 갤러리 권한, 쓰기/읽기 권한 있을 시 에러가 날 수 있음
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("RequestPermissionResult(): ", "${requestCode}, ${permissions.contentToString()}, ${grantResults.contentToString()}")
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateCamera()
                } else {
                    Toast.makeText(this, "권한을 거부하셔씁니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this).setTitle("카메라 권한이 필요합니다.").setMessage("카메라 촬영을 위해서 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create().show()
    }


}