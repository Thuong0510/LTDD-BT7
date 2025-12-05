package com.quangvinh.uploadimages.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.quangvinh.uploadimages.R;

public class MainActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView tvBack, tvId, tvUsername, tvFullname, tvEmail, tvGender;
    private Button btnLogout;

    private String id, username, fullname, email, gender, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mappingView();
        getDataFromIntent();
        setDataToView();
        addEvents();
    }

    private void mappingView() {
        imgAvatar  = findViewById(R.id.imgAvatar);
        tvBack     = findViewById(R.id.tvBack);
        tvId       = findViewById(R.id.tvId);
        tvUsername = findViewById(R.id.tvUsername);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail    = findViewById(R.id.tvEmail);
        tvGender   = findViewById(R.id.tvGender);
        btnLogout  = findViewById(R.id.btnLogout);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        id       = intent.getStringExtra("id");
        username = intent.getStringExtra("username");
        fullname = intent.getStringExtra("fullname");
        email    = intent.getStringExtra("email");
        gender   = intent.getStringExtra("gender");

        // tạm chưa có link ảnh từ server
        imageUrl = "";
    }

    private void setDataToView() {
        tvId.setText(id);
        tvUsername.setText(username);
        tvFullname.setText(fullname);
        tvEmail.setText(email);
        tvGender.setText(gender);

        // Avatar tạm dùng ảnh mặc định
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    private void addEvents() {
        tvBack.setOnClickListener(v -> onBackPressed());

        imgAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadImageActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // quay lại login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
