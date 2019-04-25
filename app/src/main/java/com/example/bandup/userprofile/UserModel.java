package com.example.bandup.userprofile;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserModel implements Serializable {

    private Uri imageUri;
    private String userName;
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private FirebaseUser firebaseUser;
    private String[] musicalInstruments;
    private String[] musicalGenres;

    public UserModel() {

    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
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
