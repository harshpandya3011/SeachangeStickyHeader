package com.seachange.healthandsafty.activity.camera

import android.util.Log
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.model.GalleryImage
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CameraPresenter(private val mView:CameraPresenterView) {

    fun uploadImage(imagePath: String, image: GalleryImage) {

        val client = OkHttpClient()
        val file = File(imagePath)
        val mediaType : MediaType? = MediaType.parse("image/jpeg")

        val requestBody : RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("imageDetails", getImageId(image))
                .addFormDataPart("image1", file.name, RequestBody.create(mediaType, file))
                .build()

        val request : Request = Request.Builder()
                .url( "http://seachangedev.azurewebsites.net/api/risk-assessments/6f8924d3-697c-4def-820e-c9ee927d24b5/images")
                .header("Accept", "application/json")
                .header("Content-Type", "multipart/form-data")
                .post(requestBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                mView.imageUploadError(e.toString())
                Log.d("TAKEN: error", e.toString())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                mView.imageUploaded("uploaded")
                Log.d("TAKEN: result", "status "  + response.code())
            }
        })
    }

    private fun getImageId(image: GalleryImage): String {

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        val time= sdf.format(currentDate)

        val arrayList = ArrayList<JSONObject>()
        val tmp = JSONObject()
        tmp.put("id", image.imageId)
        tmp.put("name", "image1")
        tmp.put("tenantId", "380")
        tmp.put("dateTimeAdded",time)
        
        arrayList.add(tmp)
        val value = arrayList.toString()
        Logger.info("""image post data: $value""")
        return value
    }
}