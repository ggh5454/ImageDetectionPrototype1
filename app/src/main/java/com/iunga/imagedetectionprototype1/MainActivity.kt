package com.iunga.imagedetectionprototype1

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
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
import androidx.core.view.isVisible
import java.net.URI


class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_GALLERY = 2
    }

    private val cameraButton: Button by lazy {
        findViewById(R.id.cameraButton)
    }
    private val imageView: ImageView by lazy {
        findViewById(R.id.imageView)
    }
    private val galleryButton: Button by lazy {
        findViewById(R.id.galleryButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCameraButton()
        initGalleryButton()
    }

    private fun initGalleryButton() {
        galleryButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigateGallery()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        500
                    )
                }
            }
        }
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
                    showPermissionContextPopup(android.Manifest.permission.CAMERA)
                }

                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
                }
            }
        }
    }

    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }


    private fun navigateCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                val imageBitmap = data?.getParcelableExtra<Bitmap>("data")
                imageView.visibility = View.VISIBLE
                imageView.setImageBitmap(imageBitmap)
            }
            REQUEST_GALLERY -> {
                val imageUri: Uri? = data?.data

                if (imageUri != null) {
                    imageView.visibility = View.VISIBLE
                    imageView.setImageURI(imageUri)

                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateCamera()
                } else {
                    Toast.makeText(this, "권한을 거부하셔씁니다.", Toast.LENGTH_SHORT).show()
                }
            }
            500 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateGallery()
                } else {
                    Toast.makeText(this, "권한을 거부하셔씁니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showPermissionContextPopup(permission: String) {
        when (permission) {
            android.Manifest.permission.CAMERA -> {
                AlertDialog.Builder(this).setTitle("카메라 권한이 필요합니다.")
                    .setMessage("카메라 촬영을 위해서 필요합니다.")
                    .setPositiveButton("동의하기") { _, _ ->
                        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
                    }
                    .setNegativeButton("취소하기") { _, _ -> }
                    .create().show()
            }
            android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                AlertDialog.Builder(this).setTitle("갤러리 권한이 필요합니다.")
                    .setMessage("갤러리 사진 선택을 위해서 필요합니다.")
                    .setPositiveButton("동의하기") { _, _ ->
                        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
                    }
                    .setNegativeButton("취소하기") { _, _ -> }
                    .create().show()
            }
        }
    }


}