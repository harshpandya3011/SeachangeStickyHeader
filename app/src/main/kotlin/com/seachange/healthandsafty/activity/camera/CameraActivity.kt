package com.seachange.healthandsafty.activity.camera

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import androidx.annotation.StringRes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.cameraview.AspectRatio
import com.google.android.cameraview.CameraView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.BaseActivity
import com.seachange.healthandsafty.activity.RASelectImageActivity
import com.seachange.healthandsafty.activity.RASelectOptionActivity
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.HazardObserver
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.GalleryImage
import com.seachange.healthandsafty.model.RiskCategory
import com.seachange.healthandsafty.utils.UtilStrings
import kotlinx.android.synthetic.main.activity_camera.*
import org.parceler.Parcels
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : BaseActivity(), Observer, CameraPresenterView {


    private val TAG = "CameraActivity"

    private val REQUEST_CAMERA_PERMISSION = 1
    private val FRAGMENT_DIALOG = "dialog"

//    private val FLASH_OPTIONS = intArrayOf(CameraView.FLASH_AUTO, CameraView.FLASH_OFF, CameraView.FLASH_ON)
//    private val FLASH_ICONS = intArrayOf(R.drawable.ic_flash_auto, R.drawable.ic_flash_off, R.drawable.ic_flash_on)
//    private val FLASH_TITLES = intArrayOf(R.string.flash_auto, R.string.flash_off, R.string.flash_on)
    private val PICK_IMAGE = 1
    private var mCurrentFlashsh: Int = 0
    private var mImageCounter: Int = 0;
    private var mCameraView: CameraView? = null
    private var mBackgroundHandler: Handler? = null
    private var selectedRisk: RiskCategory? =null
    private var mImageList:ArrayList<GalleryImage> = ArrayList()
    private var mPresenter : CameraPresenter ? = null

    private val mOnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.take_picture -> if (mCameraView != null) {
                mCameraView!!.takePicture()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera)

        mCameraView = findViewById(R.id.camera)
        if (mCameraView != null) {
            mCameraView!!.addCallback(mCallback)
        }
        val fab = findViewById<FloatingActionButton>(R.id.take_picture)
        if (fab != null) {
            fab.setOnClickListener(mOnClickListener)
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
        }

        select_gallery.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        close_camera.setOnClickListener {
            this.finish()
        }

        camera_continue.setOnClickListener {

            if (PreferenceHelper.getInstance(applicationContext).raFlow == 1) {

                HazardObserver.getInstance().isRaAdded= true
                this.finish()

            } else {

                if (mImageCounter > 1 || mImageCounter == 0) {
                    Logger.info("image list size: " + mImageList.size)
                    val intent = Intent(this, RASelectImageActivity::class.java)
                    startActivity(intent)
                } else {
                    mImageList[0].selected = true
                    PreferenceHelper.getInstance(mCtx).saveImageTakenData(mImageList)
                    val intent = Intent(this, RASelectOptionActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        camera_continue.text = this.resources.getString(R.string.fa_mail_send)
        camera_continue.typeface = SCApplication.FontMaterial()
        camera_title_icon.typeface = SCApplication.FontMaterial()

        if (PreferenceHelper.getInstance(applicationContext).raFlow == 1) {
            selectedRisk  = Parcels.unwrap<RiskCategory>(intent.getParcelableExtra<Parcelable>(UtilStrings.RISK_CATEGORY))
            camera_page_title.text = selectedRisk!!.title
            camera_page_title.visibility = View.VISIBLE
            camera_title_icon.visibility = View.VISIBLE
        } else {
            camera_page_title.visibility = View.GONE
            camera_title_icon.visibility = View.GONE
        }
        updateImageCounter()
        mImageList = PreferenceHelper.getInstance(mCtx).savedImages
        HazardObserver.getInstance().isImagesChanged = false

        mPresenter = CameraPresenter(this)
    }

    override fun onResume() {
        super.onResume()
        HazardObserver.getInstance().addObserver(this)

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            mCameraView!!.start()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment.newInstance(R.string.camera_permission_confirmation,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION,
                    R.string.camera_permission_not_granted)
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onPause() {
        mCameraView!!.stop()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler!!.looper.quitSafely()
            } else {
                mBackgroundHandler!!.looper.quit()
            }
            mBackgroundHandler = null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (permissions.size != 1 || grantResults.size != 1) {
                    throw RuntimeException("Error on requesting camera permission.")
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show()
                }
            }
        }// No need to start camera here; it is handled by onResume
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.camera_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.aspect_ratio -> {
//                val fragmentManager = supportFragmentManager
//                if ((mCameraView != null && fragmentManager.findFragmentByTag(FRAGMENT_DIALOG) == null)) {
//                    val ratios = mCameraView!!.supportedAspectRatios
//                    val currentRatio = mCameraView!!.aspectRatio
////                    AspectRatioFragment.newInstance(ratios, currentRatio)
////                            .show(fragmentManager, FRAGMENT_DIALOG)
//                }
//                return true
//            }
//            R.id.switch_flash -> {
//                if (mCameraView != null) {
//                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.size
//                    item.setTitle(FLASH_TITLES[mCurrentFlash])
//                    item.setIcon(FLASH_ICONS[mCurrentFlash])
//                    mCameraView!!.flash = FLASH_OPTIONS[mCurrentFlash]
//                }
//                return true
//            }
            R.id.switch_camera -> {
                if (mCameraView != null) {
                    val facing = mCameraView!!.facing
                    mCameraView!!.facing = if (facing == CameraView.FACING_FRONT)
                        CameraView.FACING_BACK
                    else
                        CameraView.FACING_FRONT
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onAspectRatioSelected(ratio: AspectRatio) {
        if (mCameraView != null) {
            Toast.makeText(this, ratio.toString(), Toast.LENGTH_SHORT).show()
            mCameraView!!.setAspectRatio(ratio)
        }
    }

    private fun getBackgroundHandler(): Handler {
        if (mBackgroundHandler == null) {
            val thread = HandlerThread("background")
            thread.start()
            mBackgroundHandler = Handler(thread.looper)
        }
        return mBackgroundHandler as Handler
    }

    private val mCallback = object : CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView?) {
            Log.d(TAG, "onCameraOpened")
        }

        override fun onCameraClosed(cameraView: CameraView?) {
            Log.d(TAG, "onCameraClosed")
        }

        override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
            Log.d(TAG, "onPictureTaken " + data.size)
            Toast.makeText(cameraView.context, R.string.picture_taken, Toast.LENGTH_SHORT)
                    .show()
            getBackgroundHandler().post {
                var name = System.currentTimeMillis().toString() + "_image.jpg"
                val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), name)

                var os: OutputStream? = null
                try {
                    os = FileOutputStream(file)
                    os.write(data)
                    os.close()
                    mImageCounter += 1

                    var image = GalleryImage(file.absolutePath)
                    image.name = name
                    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val current = Calendar.getInstance().time
                    image.timeFound = df.format(current)
                    image.imageId = UUID.randomUUID().toString()
                    image.hazardId = UUID.randomUUID().toString()
                    mImageList.add(image)
                    PreferenceHelper.getInstance(mCtx)!!.saveImageTakenData(mImageList)
                    runOnUiThread {
                        updateImageCounter()
                        mPresenter!!.uploadImage(file.absolutePath, image)
                    }
                    Logger.info("IMAGE: " + file.absolutePath)
                } catch (e: IOException) {
                    Log.w(TAG, "Cannot write to $file", e)
                } finally {
                    if (os != null) {
                        try {
                            os.close()
                        } catch (e: IOException) {
                            // Ignore
                        }

                    }
                }
            }
        }
    }

    override fun update(p0: Observable?, p1: Any?) {
        if (p0 is HazardObserver) {
            val observer = p0 as HazardObserver
            if (observer.isImagesChanged) {
                mImageList = PreferenceHelper.getInstance(mCtx).savedImages
                Logger.info("this is the list size: " + mImageList.size)
                mImageCounter = mImageList.size
                updateImageCounter()
            }
        }
    }

    class ConfirmationDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val args = arguments
            return AlertDialog.Builder(activity!!)
                    .setMessage(args!!.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok
                    ) { dialog, which ->
                        val permissions = args.getStringArray(ARG_PERMISSIONS)
                                ?: throw IllegalArgumentException()
                        ActivityCompat.requestPermissions(activity!!,
                                permissions, args.getInt(ARG_REQUEST_CODE))
                    }
                    .setNegativeButton(android.R.string.cancel,
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    Toast.makeText(activity,
                                            args.getInt(ARG_NOT_GRANTED_MESSAGE),
                                            Toast.LENGTH_SHORT).show()
                                }
                            })
                    .create()
        }

        companion object {

            private val ARG_MESSAGE = "message"
            private val ARG_PERMISSIONS = "permissions"
            private val ARG_REQUEST_CODE = "request_code"
            private val ARG_NOT_GRANTED_MESSAGE = "not_granted_message"

            fun newInstance(@StringRes message: Int,
                            permissions: Array<String>, requestCode: Int, @StringRes notGrantedMessage: Int): ConfirmationDialogFragment {
                val fragment = ConfirmationDialogFragment()
                val args = Bundle()
                args.putInt(ARG_MESSAGE, message)
                args.putStringArray(ARG_PERMISSIONS, permissions)
                args.putInt(ARG_REQUEST_CODE, requestCode)
                args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage)
                fragment.arguments = args
                return fragment
            }
        }

    }

    private fun updateImageCounter() {
        camera_Image_title.text = String.format(mImageCounter.toString())
//        camera_continue.isEnabled = mImageCounter>0
    }

    override fun imageUploaded(response: String) {

    }

    override fun imageUploadError(response: String) {

    }
}
