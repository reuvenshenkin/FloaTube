package com.example.reuven.FloaTube;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reuven on 09/05/2015.
 */
public class YoutubeConnector {
    private YouTube youtube;
    private YouTube.Search.List query,query2;
    Long maxResults=50l;

    // Your developer key goes here
    public static final String KEY = "AIzaSyBfqHNmAq1vsOvkTadzlmc-gVMkz47DUBw";

    public YoutubeConnector(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {


            @Override
            public void initialize(com.google.api.client.http.HttpRequest httpRequest) throws IOException {

            }

        }).setApplicationName(String.valueOf(R.string.app_name)).build();

        try{

            query = youtube.search().list("id,snippet");
            query2 = youtube.search().list("id,snippet");
            query.setKey(KEY);
            query.setType("video");
            query.setMaxResults(maxResults);
            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            query2.setKey(KEY);
            query2.setType("video");
            query2.setMaxResults(15l);
            query2.setFields("items(snippet/title)");
        }catch(IOException e){
            Log.d("YC", "Could not initialize: " + e);
        }
    }
    public List<String> autoComplete (String keys) {
        query2.setQ(keys);
        try {
            SearchListResponse response1 = query2.execute();

            List<SearchResult> results1 = response1.getItems();
            List<String> completionItems = new ArrayList<String>();
            for (SearchResult result : results1) {
                String item =result.getSnippet().getTitle();
                completionItems.add(item);
            }
            return completionItems;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
        public List<VideoItem> search(String keywords){
        query.setQ(keywords);
        try{
            SearchListResponse response = query.execute();

            List<SearchResult> results2 = response.getItems();

            List<VideoItem> items = new ArrayList<VideoItem>();
            for(SearchResult result:results2){
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getId().getVideoId());
                items.add(item);
            }
            return items;
        }catch(IOException e){
            Log.d("YC", "Could not search: "+e);
            return null;
        }
    }


}
