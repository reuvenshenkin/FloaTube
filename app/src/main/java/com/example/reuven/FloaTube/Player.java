package com.example.reuven.FloaTube;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;


public class Player extends YouTubeBaseActivity {


    public static YouTubePlayerView playerView;
    public static Context context;
    public  static String VIDEO_ID;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(getIntent().getBooleanExtra("FINISH_PLAYER",false))
        {
            finish();
        }
        else {

            context = this;
            playerView = new YouTubePlayerView(this);
            Intent intent = new Intent(getApplicationContext(), YoutubeVideoService.class);
            if (getIntent().getStringArrayListExtra("VIDEOS_ID")==null) {
                VIDEO_ID = getIntent().getStringExtra("VIDEO_ID");


                intent.putExtra("VIDEO_ID", VIDEO_ID);
            }
            else{

                intent.putExtra("POSITION",getIntent().getIntExtra("POSITION",0));
                intent.putExtra("VIDEOS_ID",getIntent().getStringArrayListExtra("VIDEOS_ID"));
            }

            startService(intent);

            moveTaskToBack(true);


        }
    }


}


