package com.bsms.securityapp.model;

public class Responses {
    private int id;
    private String date;
    private String location;
    private String details;

    private int photoCount;

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    private String photoPaths;

    public String getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(String photoPaths) {
        this.photoPaths = photoPaths;
    }

}
