package com.seachange.healthandsafty.activity.camera;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class presenter {


    public void uploadImage() {
        String UPLOAD_URL = "http://yoururl.com/example.php";

        // Example data
        String username = "test_user_123";
        String datetime = "2016-12-09 10:00:00";
        File image = new File("");

        // Create an HTTP client to execute the request
        OkHttpClient client = new OkHttpClient();

        // Create a multipart request body. Add metadata and files as 'data parts'.
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("datetime", datetime)
                .addFormDataPart("image", image.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), image))
                .build();

        // Create a POST request to send the data to UPLOAD_URL
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        // Execute the request and get the response from the server
        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check the response to see if the upload succeeded
        if (response == null || !response.isSuccessful()) {
            Log.w("Example", "Unable to upload to server.");
        } else {
            Log.v("Example", "Upload was successful.");
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TAG",response.body().string());
            }
        });

    }
}
