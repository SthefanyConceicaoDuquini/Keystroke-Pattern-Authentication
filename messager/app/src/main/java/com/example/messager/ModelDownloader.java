package com.example.messager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ModelDownloader {
    private static final String TAG = "ModelDownloader"; 
    public static void main(String[] args) {
        Log.d(TAG, "ModelDownloader está sendo executado."); 

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            
                downloadModel("http://192.168.0.114:5001/download_modelo", "modelo_ocsvm.pmml");
            }
        }, 0, 10, TimeUnit.SECONDS); 
    }

    public static void downloadModel(String modelDownloadUrl, String localModelPath) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(modelDownloadUrl);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

       
            Log.d(TAG, "Código de Status da Resposta: " + statusCode);

            if (statusCode == 200) {
               
                InputStream inputStream = response.getEntity().getContent();
                OutputStream outputStream = new FileOutputStream(localModelPath);

                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                Log.d(TAG, "Modelo baixado e salvo em: " + localModelPath);
            } else {
               
                Log.e(TAG, "Erro ao baixar o modelo. Código de resposta: " + statusCode);
            }
        } catch (IOException e) {
           
            Log.e(TAG, "Erro durante o download do modelo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
