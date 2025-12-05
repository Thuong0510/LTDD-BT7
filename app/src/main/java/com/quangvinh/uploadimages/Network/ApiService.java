package com.quangvinh.uploadimages.Network;

import com.quangvinh.uploadimages.Model.ImageResponse;
import com.quangvinh.uploadimages.Utils.Const;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("updateimages.php")
    Call<List<ImageResponse>> uploadAvatar(
            @Part(Const.MY_USERNAME) RequestBody username,   // "username"
            @Part MultipartBody.Part avatar                  // "avatar"
    );
}
