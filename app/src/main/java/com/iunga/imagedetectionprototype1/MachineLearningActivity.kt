package com.iunga.imagedetectionprototype1

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.iunga.imagedetectionprototype1.ml.Model1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class MachineLearningActivity : AppCompatActivity() {

    private val imageView1: ImageView by lazy {
        findViewById(R.id.imageView1)
    }
    private val textViewML: TextView by lazy {
        findViewById(R.id.textViewML)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_learning)


        initImageView()
    }

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


    private fun useMachineLearning(bitmap: Bitmap) {

        // Initialise the model
        try {
            val model = Model1.newInstance(this)
            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
            val imageProcessor = ImageProcessor.Builder().add(
                ResizeOp(
                    32, 32,
                    ResizeOp.ResizeMethod.BILINEAR
                )
            ).build()
            var tImage = TensorImage(DataType.FLOAT32)
            tImage.load(bitmap);
            tImage = imageProcessor.process(tImage);
            inputFeature0.loadBuffer(tImage.buffer)

            // Runs model inference and gets result.
            //TODO 100개의 label에 확률나오게 하고 확률 순으로 textView에 나타내기
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val tensorLabel = TensorLabel(
                arrayListOf(
                    "apple",
                    "aquarium_fish",
                    "baby",
                    "bear",
                    "beaver",
                    "bed",
                    "bee",
                    "beetle",
                    "bicycle",
                    "bottle",
                    "bowl",
                    "boy",
                    "bridge",
                    "bus",
                    "butterfly",
                    "camel",
                    "can",
                    "castle",
                    "caterpillar",
                    "cattle",
                    "chair",
                    "chimpanzee",
                    "clock",
                    "cloud",
                    "cockroach",
                    "couch",
                    "crab",
                    "crocodile",
                    "cup",
                    "dinosaur",
                    "dolphin",
                    "elephant",
                    "flatfish",
                    "forest",
                    "fox",
                    "girl",
                    "hamster",
                    "house",
                    "kangaroo",
                    "keyboard",
                    "lamp",
                    "lawn_mower",
                    "leopard",
                    "lion",
                    "lizard",
                    "lobster",
                    "man",
                    "maple_tree",
                    "motorcycle",
                    "mountain",
                    "mouse",
                    "mushroom",
                    "oak_tree",
                    "orange",
                    "orchid",
                    "otter",
                    "palm_tree",
                    "pear",
                    "pickup_truck",
                    "pine_tree",
                    "plain",
                    "plate",
                    "poppy",
                    "porcupine",
                    "possum",
                    "rabbit",
                    "raccoon",
                    "ray",
                    "road",
                    "rocket",
                    "rose",
                    "sea",
                    "seal",
                    "shark",
                    "shrew",
                    "skunk",
                    "skyscraper",
                    "snail",
                    "snake",
                    "spider",
                    "squirrel",
                    "streetcar",
                    "sunflower",
                    "sweet_pepper",
                    "table",
                    "tank",
                    "telephone",
                    "television",
                    "tiger",
                    "tractor",
                    "train",
                    "trout",
                    "tulip",
                    "turtle",
                    "wardrobe",
                    "whale",
                    "willow_tree",
                    "wolf",
                    "woman",
                    "worm"
                ), outputFeature0
            )


            val probabilityArray = mutableListOf<Any>()
            // getting the first label (hot dog) probability
            // if 80 (you can change that) then we are pretty sure it is a hotdog -> update UI
            val probability = tensorLabel.mapWithFloatValue

            probability.forEach { (key, value) ->
                if (value > 0) {
                    probabilityArray.add("${key}: ${value*100}%")
                }
            }
            // Releases model resources if no longer used.
            model.close()

            var consequence = ""

            for ((index, row) in probabilityArray.withIndex()){
                consequence += "$index index $row \n"
            }

            textViewML.text = consequence

        } catch (e: Exception) {
            Log.e("tfliteSupport", "Error: ", e);
        }

//
//        val model = Model1.newInstance(this)
//        // Creates inputs for reference.
//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
//        val byteBuffer = ByteBuffer.allocate(100000)
//        inputFeature0.loadBuffer(byteBuffer)
//        // Runs model inference and gets result.
//        val outputs = model.process(inputFeature0)
//        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//        // Releases model resources if no longer used.
//        model.close()
    }
}