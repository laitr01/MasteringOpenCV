package com.trach.bestapplication.opencvseries

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    enum class ActionMode{
        MEAN_BLUR,
        GAUSSIAN_BLUR,
        MEDIAN_BLUR
    }

    private val TAG = "MainActivity"
    private val PHOTO_PICKUP_RESULT = 1000
    private var srcBitmap: Bitmap? = null

    val openCVCallback = object : BaseLoaderCallback(this) {

        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    //TODO: do you work when loading is successful
                    System.loadLibrary("native-lib")
                }
                else -> super.onManagerConnected(status)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            pickAnImage()
        }

        // Example of a call to a native method

    }

    private fun pickAnImage() {
        val pickupImageIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(pickupImageIntent, PHOTO_PICKUP_RESULT)
    }

    override fun onResume() {
        super.onResume()
        //init opencv lib
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, openCVCallback)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            openCVCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_mean_blur -> {
                processBlurImage(ActionMode.MEAN_BLUR)
                true
            }
            R.id.action_gaussian_blur -> {
                processBlurImage(ActionMode.GAUSSIAN_BLUR)
                true
            }
            R.id.action_median_blur -> {
                processBlurImage(ActionMode.MEDIAN_BLUR)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_PICKUP_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val imageUri = data?.data
                    val imageStream = imageUri?.let { contentResolver.openInputStream(it) }
                    imageStream?.let {
                        BitmapFactory.decodeStream(it)?.run {
                            imageSrc.setImageBitmap(this)
                            srcBitmap = this
                        }
                    }
                } catch (ex: FileNotFoundException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private fun processBlurImage(actionMode: ActionMode) {

        srcBitmap?.let {
            //xử lý làm mờ bitmap ở đây
            //đầu tiên chuyển đổi bitmap về dạng Mat 1 đối tượng của opencv
            val src = Mat(it.height, it.width, CvType.CV_8UC4) //<- what is CvType

            Utils.bitmapToMat(it, src)

            when(actionMode){
                ActionMode.MEAN_BLUR -> Imgproc.blur(src, src, Size(100.0, 100.0))
                ActionMode.GAUSSIAN_BLUR -> Imgproc.GaussianBlur(src, src, Size(3.0,3.0), 0.0)
                ActionMode.MEDIAN_BLUR -> Imgproc.medianBlur(src, src, 3)
            }

            // ---> mat -> bitmap
            val resultBitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(src, resultBitmap)
            imageResult.setImageBitmap(resultBitmap)
        }?:let {
            Toast.makeText(baseContext, "Load image first!", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun salt()

}
