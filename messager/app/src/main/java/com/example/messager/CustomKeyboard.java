package com.example.messager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class CustomKeyboard extends LinearLayout {

    private OnKeyPressListener onKeyPressListener;
    private CustomEditText customEditText;
    private long pressStartTime;
    private float lastPressure;
    private float lastTouchArea;
    private int charactersSinceLastTraining = 0;
    private Context mContext;
    private int characterCount = 0;
    private static final int CHARACTER_THRESHOLD = 60;
    private List<KeystrokeData> inputBuffer = new ArrayList<>();
    private List<KeystrokeData> keystrokeDataList = new ArrayList<>();
    private List<KeystrokeData> temporaryDataList = new ArrayList<>();
    private List<Character> lastTenCharactersList = new ArrayList<>();
    private int charactersSinceLastSend = 0;
    private boolean trainingDataSent = false;
    private String serverUrl = "http://192.168.0.114:5001/receber_dados";
    private int exportThreshold = 10;
    private Handler downloadHandler = new Handler();

    private Runnable downloadRunnable = new Runnable() {
        @Override
        public void run() {
            downloadModel();
            downloadHandler.postDelayed(this, 24 * 60 * 60 * 1000);
        }
    };

    public interface OnKeyPressListener {
        void onKeyPress(char keyPressed, long dwellTime, float pressureDifference, float touchArea);
    }

    public CustomKeyboard(Context context) {
        this(context, null);
    }

    public CustomKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_custom_keyboard, this, true);
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (char letter : alphabet) {
            int buttonId = getResources().getIdentifier("key" + letter, "id", context.getPackageName());
            Button keyButton = findViewById(buttonId);
            keyButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    handleKeyPress(letter, event);
                    return true;
                }
            });
        }
        Button keySpaceButton = findViewById(R.id.keySpace);
        keySpaceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleKeyPress(' ', event);
                return true;
            }
        });
        Button keyBackspaceButton = findViewById(R.id.keyBackspace);
        keyBackspaceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleKeyPress('\b', event);
                return true;
            }
        });
        downloadHandler.postDelayed(downloadRunnable, 1000);
    }

    public void setOnKeyPressListener(OnKeyPressListener listener) {
        this.onKeyPressListener = listener;
    }

    public void attachTo(final CustomEditText customEditText) {
        this.customEditText = customEditText;
        customEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomKeyboard(customEditText);
            }
        });
    }

    private void showCustomKeyboard(CustomEditText customEditText) {
        if (customEditText != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(customEditText.getWindowToken(), 0);
            customEditText.requestFocus();
            imm.showSoftInput(customEditText, InputMethodManager.SHOW_FORCED);
        }
    }

    private void handleKeyPress(char keyPressed, MotionEvent event) {
        if (onKeyPressListener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressStartTime = System.currentTimeMillis();
                    lastPressure = event.getPressure();
                    lastTouchArea = event.getSize();
                    break;
                case MotionEvent.ACTION_UP:
                    if (pressStartTime != 0) {
                        charactersSinceLastSend++;
                        long dwellTime = System.currentTimeMillis() - pressStartTime;
                        float pressureDifference = Math.abs(lastPressure - event.getPressure());
                        float touchArea = event.getSize();
                        KeystrokeData keystrokeData = new KeystrokeData(dwellTime, pressureDifference, touchArea);
                        keystrokeDataList.add(keystrokeData);
                        temporaryDataList.add(keystrokeData);
                        charactersSinceLastSend++;
                        pressStartTime = 0;
                        lastPressure = 0;
                        lastTouchArea = 0;
                        if (calculateDataSizeInBytes(keystrokeDataList) >= exportThreshold) {
                            String csvFilename = getContext().getExternalFilesDir(null) + "/keystroke_data.csv";
                            exportKeystrokeDataToCSV(keystrokeDataList, csvFilename);

                                sendKeystrokeDataToServer(csvFilename);
                                //boolean isUser = applyModel(temporaryDataList);
                            keystrokeDataList.clear();

                            charactersSinceLastSend = 0;
                        }
                        onKeyPressListener.onKeyPress(keyPressed, dwellTime, pressureDifference, touchArea);
                    }
                    break;

            }
        }
    }



    public void markTrainingDataSent() {
        trainingDataSent = true;
    }

    private int calculateDataSizeInBytes(List<KeystrokeData> data) {
        int dataSize = 0;
        for (KeystrokeData keystrokeData : data) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(keystrokeData);
                objectOutputStream.flush();
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                int objectSize = byteArray.length;
                dataSize += objectSize;
                objectOutputStream.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataSize;
    }

    public List<KeystrokeData> readKeystrokeDataFromFile(Context context, String filename) {
        List<KeystrokeData> loadedData = new ArrayList<>();
        try {
            ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput(filename));
            loadedData = (List<KeystrokeData>) inputStream.readObject();
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("CustomKeyboard", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e("CustomKeyboard", "Error reading file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("CustomKeyboard", "Class not found while deserializing: " + e.getMessage());
        }
        return loadedData;
    }

    public void exportKeystrokeDataToCSV(List<KeystrokeData> keystrokeDataList, String csvFilename) {
        try {
            FileWriter writer = new FileWriter(csvFilename);
            for (KeystrokeData data : keystrokeDataList) {
                writer.write(data.getDwellTime() + "," + data.getPressureDifference() + "," + data.getTouchArea() + "\n");
            }
            writer.close();
            Log.d("CustomKeyboard", "Data exported to CSV: " + csvFilename);
        } catch (IOException e) {
            Log.e("CustomKeyboard", "Error exporting data to CSV: " + e.getMessage());
        }
    }

    public void viewCSVData(String csvFilename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String keyPressed = fields[0];
                long dwellTime = Long.parseLong(fields[1]);
                float pressureDifference = Float.parseFloat(fields[2]);
                float touchArea = Float.parseFloat(fields[3]);
                Log.d("CSVData", "Dwell Time: " + dwellTime + " ms");
                Log.d("CSVData", "Pressure Difference: " + pressureDifference);
                Log.d("CSVData", "Touch Area: " + touchArea);
            }
            reader.close();
        } catch (IOException e) {
            Log.e("CustomKeyboard", "Error reading CSV file: " + e.getMessage());
        }
    }

    private void sendKeystrokeDataToServer(final String csvFilename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder csvDataBuilder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new FileReader(csvFilename));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        csvDataBuilder.append(line).append("\n");
                    }
                    reader.close();
                    String csvData = csvDataBuilder.toString();
                    URL url = new URL(serverUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write("dados=" + csvData);
                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("CustomKeyboard", "Data sent to server successfully.");
                            }
                        });
                    } else {
                        Log.e("CustomKeyboard", "Error sending data to server. Response code: " + responseCode);
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("CustomKeyboard", "Error sending data to server: " + e.getMessage());
                }
            }
        }).start();
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private void downloadModel() {
        String modelUrl = "http://192.168.0.114:5001/download_modelo";
        final String destinationPath = mContext.getFilesDir() + File.separator + "modelo_ocsvm.pmml";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .build();
                    Request request = new Request.Builder()
                            .url(modelUrl)
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        InputStream inputStream = response.body().byteStream();
                        FileOutputStream outputStream = new FileOutputStream(new File(destinationPath));
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        inputStream.close();
                        Log.d("CustomKeyboard", "Model downloaded successfully to: " + destinationPath);
                    } else {
                        Log.e("CustomKeyboard", "Model download failed. Response code: " + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("CustomKeyboard", "Error during model download: " + e.getMessage());
                }
            }
        }).start();
    }

}
