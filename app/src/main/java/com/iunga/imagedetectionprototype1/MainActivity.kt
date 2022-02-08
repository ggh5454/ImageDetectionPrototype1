package com.iunga.imagedetectionprototype1

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.internal.ContextUtils.getActivity
import com.iunga.imagedetectionprototype1.ml.Model1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer


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
        var intent = Intent(this, MachineLearningActivity::class.java)
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