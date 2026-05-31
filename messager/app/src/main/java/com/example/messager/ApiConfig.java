package com.example.messager;

public final class ApiConfig {
    // Android emulator alias for the host machine running the Flask server.
    private static final String SERVER_HOST = "10.0.2.2";
    private static final int SERVER_PORT = 5001;
    private static final String BASE_URL = "http://" + SERVER_HOST + ":" + SERVER_PORT + "/";

    private ApiConfig() {
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getReceiveDataUrl() {
        return BASE_URL + "receber_dados";
    }

    public static String getDownloadModelUrl() {
        return BASE_URL + "download_modelo";
    }
}
