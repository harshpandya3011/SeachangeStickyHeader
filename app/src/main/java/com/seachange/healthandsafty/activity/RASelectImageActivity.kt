package com.seachange.healthandsafty.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.GalleryFragment

class RASelectImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_raselect_image)

        val galleryFragment = GalleryFragment.newInstance(3)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.gallery_framelayout, galleryFragment, "gallery_fragment")
                    .commit()
        }
    }
}
