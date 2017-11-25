package com.example.dell.model;

/**
 * Created by DELL on 3/19/2017.
 */

public class MuseumView {
    private int id;
    private String image;
    private String name;
    private String address;
    private String path;
    private String content;
    private double latitude;
    private double longitude;

    public MuseumView() {
    }

    public MuseumView(int id, String image, String name, String address, String path, String content, double latitude, double longitude) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.address = address;
        this.path = path;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
