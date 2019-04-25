package com.example.bandup.userprofile;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserModel implements Serializable {

    private String imageUrl;
    private String username;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
