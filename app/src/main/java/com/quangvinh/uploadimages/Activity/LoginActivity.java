package com.quangvinh.uploadimages.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quangvinh.uploadimages.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtId, edtUsername, edtFullname, edtEmail, edtGender;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mappingView();
        addEvents();
    }

    private void mappingView() {
        edtId       = findViewById(R.id.edtId);
        edtUsername = findViewById(R.id.edtUsername);
        edtFullname = findViewById(R.id.edtFullname);
        edtEmail    = findViewById(R.id.edtEmail);
        edtGender   = findViewById(R.id.edtGender);
        btnLogin    = findViewById(R.id.btnLogin);
    }

    private void addEvents() {
        btnLogin.setOnClickListener(v -> {
            String id       = edtId.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String fullname = edtFullname.getText().toString().trim();
            String email    = edtEmail.getText().toString().trim();
            String gender   = edtGender.getText().toString().trim();

            // kiểm tra đơn giản
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullname)) {
                Toast.makeText(this, "Vui lòng nhập ít nhất username và họ tên",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // tạo Intent sang ProfileActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("username", username);
            intent.putExtra("fullname", fullname);
            intent.putExtra("email", email);
            intent.putExtra("gender", gender);
            startActivity(intent);
        });
    }
}
