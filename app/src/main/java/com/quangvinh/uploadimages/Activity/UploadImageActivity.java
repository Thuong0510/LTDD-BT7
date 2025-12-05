package com.quangvinh.uploadimages.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.quangvinh.uploadimages.Model.ImageResponse;
import com.quangvinh.uploadimages.Network.RetrofitClient;
import com.quangvinh.uploadimages.R;
import com.quangvinh.uploadimages.Utils.Const;
import com.quangvinh.uploadimages.Utils.RealPathUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageActivity extends AppCompatActivity {

    private Button btnChoose, btnUpload;
    private ImageView imgChoose;

    private Uri mUri;
    private ProgressDialog progressDialog;

    private String mUsername; // truyền từ màn Profile sang

    private static final int REQUEST_CODE_STORAGE = 123;

    // list quyền (giống slide – dùng cho Android < 13)
    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private ActivityResultLauncher<Intent> mActivityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {

                            Uri uri = result.getData().getData();
                            if (uri == null) return;

                            mUri = uri;
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        getContentResolver(), uri);
                                imgChoose.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Không đọc được ảnh", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);

        // nhận username/id gửi sang
        mUsername = getIntent().getStringExtra("username");
        if (mUsername == null) mUsername = "";

        mappingView();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        btnChoose.setOnClickListener(v -> {
            if (checkPermission()) {
                openGallery();
            }
        });

        btnUpload.setOnClickListener(v -> {
            if (mUri != null) {
                uploadImageToServer();
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mappingView() {
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imgChoose = findViewById(R.id.imgChoose);
    }

    // mở thư viện ảnh
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    // kiểm tra & xin quyền
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        // Android 13+ dùng READ_MEDIA_IMAGES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_CODE_STORAGE);
                return false;
            }
            return true;
        }

        // Android < 13: dùng READ/WRITE_EXTERNAL_STORAGE
        for (String p : storage_permissions) {
            if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(storage_permissions, REQUEST_CODE_STORAGE);
                return false;
            }
        }
        return true;
    }

    // nhận kết quả xin quyền
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // hàm upload
    private void uploadImageToServer() {
        if (mUri == null) {
            Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // lấy đường dẫn thật
        String realPath = RealPathUtil.getRealPath(this, mUri);
        if (realPath == null) {
            Toast.makeText(this, "Không lấy được đường dẫn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(realPath);
        if (!file.exists()) {
            Toast.makeText(this, "File ảnh không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // part cho file
        RequestBody requestFile = RequestBody.create(
                okhttp3.MediaType.parse("multipart/form-data"),
                file
        );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(Const.MY_IMAGES, file.getName(), requestFile);
        // Const.MY_IMAGES = "avatar"

        // part cho username / id
        RequestBody username =
                RequestBody.create(
                        okhttp3.MediaType.parse("multipart/form-data"),
                        mUsername
                );

        // Gọi API qua RetrofitClient
        RetrofitClient.getApiService()
                .uploadAvatar(username, body)
                .enqueue(new Callback<List<ImageResponse>>() {
                    @Override
                    public void onResponse(Call<List<ImageResponse>> call,
                                           Response<List<ImageResponse>> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(UploadImageActivity.this,
                                    "Upload thành công", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(UploadImageActivity.this,
                                    "Upload thất bại", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ImageResponse>> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(UploadImageActivity.this,
                                "Lỗi API: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
