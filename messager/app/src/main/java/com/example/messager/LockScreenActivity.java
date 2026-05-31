package com.example.messager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LockScreenActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private Button buttonUnlock;
    private String savedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        editTextPassword = findViewById(R.id.editTextPassword);
        buttonUnlock = findViewById(R.id.buttonUnlock);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        savedPassword = sharedPreferences.getString("password", "");

        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredPassword = editTextPassword.getText().toString();
                if (enteredPassword.equals(savedPassword)) {

                    Toast.makeText(LockScreenActivity.this, "Senha correta. Desbloqueando...", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(LockScreenActivity.this, "Senha incorreta. Tente novamente.", Toast.LENGTH_SHORT).show();
                    editTextPassword.setText("");
                }
            }
        });
    }
}
