package com.iunga.imagedetectionprototype1

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iunga.imagedetectionprototype1.ml.Efficientnetb0Model1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*
import androidx.annotation.RequiresApi as RequiresApi1

class MachineLearningActivity : AppCompatActivity() {

    private val imageView1: ImageView by lazy {
        findViewById(R.id.imageView1)
    }
    private val textViewML: TextView by lazy {
        findViewById(R.id.textViewML)


    }

    @RequiresApi1(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_learning)


        initImageView()
    }

    @RequiresApi1(Build.VERSION_CODES.N)
    private fun initImageView() {
        val imageUri = intent.getParcelableExtra<Uri>("Uri")
        val imageBitmap = intent.getParcelableExtra<Bitmap>("Bitmap")

        if (imageBitmap != null) {
            imageView1.setImageBitmap(imageBitmap)
            useMachineLearning(imageBitmap)
        }
        if (imageUri != null) {
            val imageBitmap1 = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            imageView1.setImageBitmap(imageBitmap1)
            useMachineLearning(imageBitmap1)
        }

    }

    private fun readLabelTextFile(): ArrayList<String> {
        val inputStream = resources.openRawResource(R.raw.label)
        val scanner = Scanner(inputStream, "UTF-8")
        var index = 0
        val labelArray = arrayListOf<String>()
        while (scanner.hasNextLine()) {
            labelArray.add(scanner.nextLine())
        }
        return labelArray
    }


    @RequiresApi1(Build.VERSION_CODES.N)
    private fun useMachineLearning(bitmap: Bitmap) {

        val model = Efficientnetb0Model1.newInstance(this)
        // Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 120, 120, 3), DataType.FLOAT32)
        val imageProcessor = ImageProcessor.Builder().add(
            ResizeOp(120, 120, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)
        ).add(
            NormalizeOp(
                0.0f,
                255.0f
            )

        ).build()
        var tImage = TensorImage(DataType.FLOAT32)

        tImage.load(bitmap);
        tImage = imageProcessor.process(tImage)


        inputFeature0.loadBuffer(tImage.buffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val labelArray = readLabelTextFile()

        val tensorLabel = TensorLabel(
            labelArray, outputFeature0
        )

        val probabilityMap = mutableMapOf<String, Float>()
        val probability = tensorLabel.mapWithFloatValue
        var consequence = ""
        // Releases model resources if no longer used.
        model.close()

        probability.forEach { (key, value) ->
            probabilityMap[key] = value
        }
        val sortedMapByValue =
            probabilityMap.toList().sortedWith(compareBy { it.second }).reversed().toMap()


        sortedMapByValue.forEach { (key, value) ->
            if (value > 0.1) {
                consequence += "$key $value \n"
            }
        }

        textViewML.movementMethod = ScrollingMovementMethod()
        textViewML.text = consequence


    }
}