package com.example.dell.model;

public class ArtifactView {
    private int id;
    private String title;
    private String image;
    private String address;

    public ArtifactView() {
    }

    public ArtifactView(int id, String title, String image, String address) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.address = address;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
