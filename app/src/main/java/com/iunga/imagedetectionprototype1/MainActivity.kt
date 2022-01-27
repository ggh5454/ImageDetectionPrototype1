package com.iunga.imagedetectionprototype1

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val cameraButton: Button by lazy {
        findViewById(R.id.cameraButton)
    }
    private val imageView: View by lazy {
        findViewById(R.id.imageView)
    }
    val PERM_STORAGE = 99
    val PERM_CAMERA = 100
    val REQ_CAMERA = 101

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
                    showPermissionConntextPopup()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
                }
            }
        }
    }

    private fun navigateCamera() {
        //TODO 카메라 권한 요청 후 카메라 실행 + 촬영 후 이미지 뷰에 이미지
    }

    private fun showPermissionConntextPopup() {
        AlertDialog.Builder(this).setTitle("카메라 권한이 필요합니다.").setMessage("카메라 촬영을 위해서 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create().show()
    }


}