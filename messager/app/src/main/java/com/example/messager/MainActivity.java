package com.example.messager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomKeyboard.OnKeyPressListener {

    private CustomKeyboard customKeyboard;
    private CustomEditText customEditTextMessage;
    private RecyclerView recyclerViewMessages;
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private List<KeystrokeData> keystrokeDataList = new ArrayList<>();
    private long pressStartTime;
    private float lastPressure;
    private float lastTouchArea;
    private static final int SET_PASSWORD_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String password = sharedPreferences.getString("password", null);
        if (password == null) {
            Intent intent = new Intent(MainActivity.this, SetPasswordActivity.class);
            startActivityForResult(intent, SET_PASSWORD_REQUEST_CODE);
        } else {
            setContentView(R.layout.activity_main);
            initializeUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_PASSWORD_REQUEST_CODE && resultCode == RESULT_OK) {
            initializeUI();
        }
    }

    private void initializeUI() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        customEditTextMessage = findViewById(R.id.customEditTextMessage);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
        customKeyboard = new CustomKeyboard(this);
        customKeyboard.setContext(this);
        customKeyboard.setOnKeyPressListener(this);
        customKeyboard.attachTo(customEditTextMessage);
        customKeyboard.setOnKeyPressListener(this);
        customKeyboard.attachTo(customEditTextMessage);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = customEditTextMessage.getText().toString().trim();
                if (!messageContent.isEmpty()) {
                    messages.add(new Message("Você", messageContent));
                    messageAdapter.notifyItemInserted(messages.size() - 1);
                    customEditTextMessage.setText("");
                    recyclerViewMessages.scrollToPosition(messages.size() - 1);
                }
            }
        });
        LinearLayout layoutKeyboardContainer = findViewById(R.id.layoutKeyboardContainer);
        layoutKeyboardContainer.addView(customKeyboard);
    }

    @Override
    public void onKeyPress(char keyPressed, long dwellTime, float pressureDifference, float touchArea) {
        Log.d("CustomKeyboard", "Dwell Time: " + dwellTime + " ms");
        Log.d("CustomKeyboard", "Pressure Difference: " + pressureDifference);
        Log.d("CustomKeyboard", "Touch Area: " + touchArea);
        if (customEditTextMessage != null) {
            if (keyPressed == ' ') {
                customEditTextMessage.append(" ");
            } else if (keyPressed == '\b') {
                String currentText = customEditTextMessage.getText().toString();
                if (!TextUtils.isEmpty(currentText)) {
                    customEditTextMessage.setText(currentText.substring(0, currentText.length() - 1));
                    customEditTextMessage.setSelection(customEditTextMessage.getText().length());
                }
            } else {
                customEditTextMessage.append(Character.toString(keyPressed));
            }
        }
    }
}
