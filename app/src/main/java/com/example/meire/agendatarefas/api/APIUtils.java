package com.example.meire.agendatarefas.api;

public class APIUtils {
    public static final String BASE_URL = "http://www.mocky.io/v2/";

    public static LoginAPI getLoginService()
    {
        return RetrofitClient.getClient(BASE_URL).create(LoginAPI.class);
    }
}
