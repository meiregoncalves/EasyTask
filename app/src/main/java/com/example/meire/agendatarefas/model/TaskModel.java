package com.example.meire.agendatarefas.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class TaskModel implements Parcelable {

    private int id;
    private String title;
    private String description;
    private String adress;
    private String phone;
    private String done;
    private String adress_name;
    private Double latitude = new Double(0);
    private Double longitude = new Double(0);
    private String datetime_reminder;
    private int repetition;

    public TaskModel()
    {

    }

    protected TaskModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        adress = in.readString();
        phone = in.readString();
        done = in.readString();
        adress_name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        datetime_reminder = in.readString();
        repetition = in.readInt();
    }

    public static final Creator<TaskModel> CREATOR = new Creator<TaskModel>() {
        @Override
        public TaskModel createFromParcel(Parcel in) {
            return new TaskModel(in);
        }

        @Override
        public TaskModel[] newArray(int size) {
            return new TaskModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdress_name() {
        return adress_name;
    }

    public void setAdress_name(String adress_name) {
        this.adress_name = adress_name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public String getDatetime_reminder() {
        return datetime_reminder;
    }

    public void setDatetime_reminder(String datetime_reminder) {
        this.datetime_reminder = datetime_reminder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(adress);
        parcel.writeString(phone);
        parcel.writeString(done);
        parcel.writeString(adress_name);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(datetime_reminder);
        parcel.writeInt(repetition);
    }
}
