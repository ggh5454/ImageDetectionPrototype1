package com.iunga.imagedetectionprototype1

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


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

    //초기에 권한 요청
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

    //초기에 권한 요청
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

    //gallery 이용
    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    //camera 이용
    private fun navigateCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    //camera, gallery 이용 결과 imageView에 사진 올리기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        val intent = Intent(this, MachineLearningActivity::class.java)
        when (requestCode) {

            REQUEST_IMAGE_CAPTURE -> {
                val imageBitmap = data?.getParcelableExtra<Bitmap>("data")
                imageView.visibility = View.VISIBLE
                imageView.setImageBitmap(imageBitmap)
                intent.putExtra("Bitmap", imageBitmap)
                if (imageBitmap != null) {
                    startActivity(intent)
                }
            }
            REQUEST_GALLERY -> {
                val imageUri: Uri? = data?.data
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                if (imageUri != null) {
                    imageView.visibility = View.VISIBLE
                    imageView.setImageBitmap(imageBitmap)
                    intent.putExtra("Uri", imageUri)
                }
                if (imageBitmap != null) {
                    startActivity(intent)
                }
            }
        }

    }

    //결과 요청
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
                    Toast.makeText(this, getString(R.string.denyPermission), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            500 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateGallery()
                } else {
                    Toast.makeText(this, getString(R.string.denyPermission), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun showPermissionContextPopup(permission: String) {
        when (permission) {
            android.Manifest.permission.CAMERA -> {
                AlertDialog.Builder(this).setTitle(getString(R.string.requestCameraPermission))
                    .setMessage(getString(R.string.reasonRequestCameraPermission))
                    .setPositiveButton(getString(R.string.Agree)) { _, _ ->
                        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
                    }
                    .setNegativeButton(getString(R.string.Cancel)) { _, _ -> }
                    .create().show()
            }
            android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                AlertDialog.Builder(this).setTitle(getString(R.string.requestGalleryPermission))
                    .setMessage(getString(R.string.reasonRequestGalleryPermission))
                    .setPositiveButton(R.string.Agree) { _, _ ->
                        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
                    }
                    .setNegativeButton(R.string.Cancel) { _, _ -> }
                    .create().show()
            }
        }
    }


}