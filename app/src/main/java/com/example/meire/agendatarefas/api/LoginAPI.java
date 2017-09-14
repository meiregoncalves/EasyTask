package com.example.meire.agendatarefas.api;

import com.example.meire.agendatarefas.model.LoginModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoginAPI {
    @GET("58b9b1740f0000b614f09d2f")
    Call<LoginModel> getLogin();
}
