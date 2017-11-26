package com.example.dell.model;

import java.util.ArrayList;

public class FavoriteMuseumView {
    private int id;
    private String image;
    private String name;
    private String address;
    private String path;
    private ArrayList<ArtifactView> artifactViews = new ArrayList<>();

    public FavoriteMuseumView() {
    }

    public FavoriteMuseumView(int id, String image, String name, String address, String path, ArrayList<ArtifactView> artifactViews) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.address = address;
        this.path = path;
        this.artifactViews = artifactViews;
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

    public ArrayList<ArtifactView> getArtifactViews() {
        return artifactViews;
    }

    public void setArtifactViews(ArrayList<ArtifactView> artifactViews) {
        this.artifactViews = artifactViews;
    }
}
