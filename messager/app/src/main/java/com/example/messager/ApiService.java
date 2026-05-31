package com.example.messager; // Substitua pelo pacote correto

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("verificar_modelo")
    Call<Boolean> checkModelAvailability();


}
