package com.example.reuven.FloaTube;

/**
 * Created by reuven on 23/05/2015.
 */
public class UserSearch {
    private long id;
    private String userSearch;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserSearch() {
        return userSearch;
    }

    public void setUserSearch(String userSearch) {
        this.userSearch = userSearch;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return userSearch;
    }

}
