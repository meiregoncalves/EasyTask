package com.example.meire.agendatarefas.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginModel implements Parcelable {
    private String usuario;
    private String senha;

    protected LoginModel(Parcel in) {
        usuario = in.readString();
        senha = in.readString();
    }

    public static final Creator<LoginModel> CREATOR = new Creator<LoginModel>() {
        @Override
        public LoginModel createFromParcel(Parcel in) {
            return new LoginModel(in);
        }

        @Override
        public LoginModel[] newArray(int size) {
            return new LoginModel[size];
        }
    };

    public String getUser() {
        return usuario;
    }

    public void setUser(String user) {
        this.usuario = user;
    }

    public String getPassword() {
        return senha;
    }

    public void setPassword(String password) {
        this.senha = password;
    }

    public LoginModel()
    {
        this.setPassword("");
        this.setUser("");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(usuario);
        parcel.writeString(senha);
    }
}
