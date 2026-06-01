package com.example.messager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetPasswordActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private Button buttonSetPassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSetPassword = findViewById(R.id.buttonSetPassword);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        buttonSetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", password);
                    editor.apply();

                    Intent intent = new Intent(SetPasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SetPasswordActivity.this, "Por favor, insira uma senha", Toast.LENGTH_SHORT).show();
                }
    }
        });
    }
}

