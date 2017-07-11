package com.example.reuven.FloaTube;



/**
 * Created by reuven on 09/05/2015.
 */
public class VideoItem{


    private long rowId;
    private String title;
    private String description;
    private String thumbnailURL;
    private String id;
    private int length;
    private boolean isCheckBoxVisible=false;
    private boolean isChecked=false;
    private boolean isMenuButtonVisible=false;
    private boolean isClicked=false;

    public boolean isClicked() {
        return isClicked;
    }

    public void setIsClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


    public boolean isCheckBoxVisible() {
        return isCheckBoxVisible;
    }

    public void setIsCheckBoxVisible(boolean isCheckBoxVisible) {
        this.isCheckBoxVisible = isCheckBoxVisible;
    }

    public boolean isMenuButtonVisible() {
        return isMenuButtonVisible;
    }

    public void setIsMenuButtonVisible(boolean isMenuButtonVisible) {
        this.isMenuButtonVisible = isMenuButtonVisible;
    }


    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

}


