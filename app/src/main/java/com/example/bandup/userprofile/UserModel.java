package com.example.bandup.userprofile;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserModel implements Serializable {

    private String imageUri;
    private String userName;
    private String firstName;
    private String lastName;
    private Integer age;
    private Integer birthDay;
    private Integer birthMonth;
    private Integer birthYear;
    private String uid;
    private String[] musicalInstruments;
    private String[] musicalGenres;

    public UserModel() {

    }

    public Integer getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Integer birthDay) {
        this.birthDay = birthDay;
    }

    public Integer getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(Integer birthMonth) {
        this.birthMonth = birthMonth;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String[] getMusicalInstruments() {
        return musicalInstruments;
    }

    public void setMusicalInstruments(String[] musicalInstruments) {
        this.musicalInstruments = musicalInstruments;
    }

    public String[] getMusicalGenres() {
        return musicalGenres;
    }

    public void setMusicalGenres(String[] musicalGenres) {
        this.musicalGenres = musicalGenres;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Uri getImageUri() {
        return Uri.parse(imageUri);
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri.toString();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
