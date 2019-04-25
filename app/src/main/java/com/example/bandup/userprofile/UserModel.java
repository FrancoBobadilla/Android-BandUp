package com.example.bandup.userprofile;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserModel implements Serializable {

    private String imageUrl;
    private String name;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
