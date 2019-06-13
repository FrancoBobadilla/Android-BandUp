package com.example.bandup.post;

import android.net.Uri;

public class PostModel {
    private String postId;
    private Uri postFile;
    private String title;
    private String description;
    private String publisher;
    private String timestamp;

    public PostModel() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Uri getPostFile() {
        return postFile;
    }

    public void setPostFile(Uri postFile) {
        this.postFile = postFile;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
