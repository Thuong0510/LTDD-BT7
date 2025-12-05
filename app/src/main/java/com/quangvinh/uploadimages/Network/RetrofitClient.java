package com.quangvinh.uploadimages.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://app.iotstar.vn:8081/appfoods/";

    private static Retrofit retrofit;

    // tạo Retrofit dùng singleton
    private static Retrofit getInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    // Hàm tiện để lấy ApiService
    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}
