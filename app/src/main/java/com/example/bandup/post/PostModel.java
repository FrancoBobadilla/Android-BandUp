package com.example.bandup.post;

public class PostModel {
    private String postId;
    private String postFile;
    private String title;
    private String description;
    private String publisher;

    public PostModel(String postId, String postFile, String title, String description, String publisher) {
        this.postId = postId;
        this.postFile = postFile;
        this.title = title;
        this.description = description;
        this.publisher = publisher;
    }

    public PostModel() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostFile() {
        return postFile;
    }

    public void setPostFile(String postFile) {
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
}
