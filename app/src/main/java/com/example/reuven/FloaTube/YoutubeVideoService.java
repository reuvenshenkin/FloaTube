package com.example.reuven.FloaTube;


import android.annotation.TargetApi;
import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

public class YoutubeVideoService extends Service implements View.OnTouchListener,GestureDetector.OnGestureListener, YouTubePlayer.OnInitializedListener ,YouTubePlayer.PlayerStateChangeListener,YouTubePlayer.OnFullscreenListener {


    private YouTubePlayerView playerView;
    private YouTubePlayer youTubePlayer;
    private WindowManager windowManager;
    private ImageView playerViewCloseButton;
    private WindowManager.LayoutParams params,params2;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private int width ;
    private int height;
    private boolean isWindowClosed;
    private boolean isServiceFirstTimeStarted=true;
    private OrientationEventListener orientationListener;
    boolean isPortrait,isLandscape,isReverseLandscape;
    private String videoId;
    private int position;
    private ArrayList<String> videosId;
    public YoutubeVideoService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        BroadcastReceiver mReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//                    youTubePlayer.pause();
//                } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
//                    youTubePlayer.play();
//                }
//            }
//        };
//        registerReceiver(mReceiver, filter);
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
//        registerReceiver(mReceiver, intentFilter);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        playerViewCloseButton = new ImageView(this);
        playerViewCloseButton.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
        playerViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationListener.disable();
                removeViewsFromWindowManager();
                isWindowClosed = true;

                Intent intent = new Intent(getApplicationContext(), Player.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("FINISH_PLAYER", true);

                startActivity(intent);

                stopSelf();


            }
        });
       getDisplaySize();
       initPlayerViewParams();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getStringArrayListExtra("VIDEOS_ID")==null) {
            videoId = intent.getStringExtra("VIDEO_ID");
        }
        else {
            position = intent.getIntExtra("POSITION", 0);
            videosId = intent.getStringArrayListExtra("VIDEOS_ID");
        }
        if (isServiceFirstTimeStarted || isWindowClosed ) {
            playerView= Player.playerView;

            if (playerView!=null){
                View v=playerView.getChildAt(0);
                v.setOnTouchListener(this);
                playerView.setFocusable(true);
                playerView.setFocusableInTouchMode(true);
                playerView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                playerView.setOnTouchListener(YoutubeVideoService.this);
                playerView.initialize(YoutubeConnector.KEY, this);
                addViewsToWindowManager();
            }

            isServiceFirstTimeStarted=false;
            isWindowClosed=false;
        }
        else {
           removeViewsFromWindowManager();

            playerView = Player.playerView;
            View v=playerView.getRootView();
            v.setOnTouchListener(this);
            playerView.setFocusable(true);
            playerView.setFocusableInTouchMode(true);
            playerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            playerView.setOnTouchListener(YoutubeVideoService.this);
            playerView.initialize(YoutubeConnector.KEY, this);
            addViewsToWindowManager();
            isServiceFirstTimeStarted=false;
        }
        orientationListener=new OrientationEventListener(getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
               if(((orientation<15 || orientation>345) && !isPortrait)){
                   isPortrait=true;
                   isLandscape=false;
                   isReverseLandscape=false;
                    setSmallPlayerView();
               }

               else if (orientation<285 && orientation>255 && !isLandscape ) {
                   isLandscape=true;
                   isReverseLandscape=false;
                   isPortrait=false;
                   setFullscreenPlayerView();

               }
                else if (orientation<105 && orientation>75 && !isReverseLandscape ) {
                   isReverseLandscape = true;
                   isLandscape = false;
                   isPortrait = false;
                   setFullscreenPlayerView();
               }
                updatePlayerViewCloseButton();
                updateWindowManager();

            }
        };
        orientationListener.enable();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!isWindowClosed) {
            removeViewsFromWindowManager();
            Intent intent = new Intent(getApplicationContext(), Player.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("FINISH_PLAYER", true);

            startActivity(intent);
            playerViewCloseButton=null;
            params=null;
            params2=null;
            windowManager=null;
        }
    }


    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean restored) {
        if(!restored) {
            if (videosId==null) {
                player.loadVideo(videoId);
            }
            else {
                player.loadVideo(videosId.get(position));
                player.setPlayerStateChangeListener(this);
            }
            player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
           // player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            player.setOnFullscreenListener(this);
            youTubePlayer = player;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        playerView.requestDisallowInterceptTouchEvent(true);

//        playerView.onInterceptTouchEvent(event);
//        playerView.onTouchEvent(event);

// dragging the playerView
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updatePlayerViewCloseButton();
                updateWindowManager();
                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;
            case MotionEvent.ACTION_UP:

                return true;
            case MotionEvent.ACTION_MOVE:
                playerView.requestDisallowInterceptTouchEvent(true);
                getDisplaySize();
                if (params.width == WindowManager.LayoutParams.MATCH_PARENT) {
                    params.width = width;
                    if (height < width) {
                        params.height = (int) (height * 0.90);
                    } else {
                        params.height = (int) (height * 0.36);
                    }
                }
                if ((initialX + (int) (event.getRawX() - initialTouchX)) <= (width - params.width)) {
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                }
                if ((initialY + (int) (event.getRawY() - initialTouchY)) <= (height - params.height - (params2.height * 0.55))) {
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                }

                if (params.width == width) {
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                }

//setting the playerViewCloseButton to the top left of the playerView
                updatePlayerViewCloseButton();
                updateWindowManager();
                Toast.makeText(this, "move", Toast.LENGTH_SHORT).show();
                return true;
        }


        return false;
    }

    @Override
    public void onFullscreen(boolean b) {

        if (b){
            setFullscreenPlayerView();
        }
        else {
            setSmallPlayerView();
        }



    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getDisplaySize(){
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }
    private void initPlayerViewParams() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                ,
                PixelFormat.TRANSLUCENT
        );
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        params.gravity = Gravity.TOP | Gravity.START;
        params.x=0;
        params.y=0;

        params2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params2.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        params2.gravity = Gravity.TOP | Gravity.START;

        if (height>width){
            params.y= (int) (height*0.2);
            params.width = (int) (width * 0.65);
            params.height=(int) (width * 0.35);
            params2.width= (int) (height*0.07);
            params2.height= (int) (height*0.07);
        }
        else{
            params.y= (int) (height*0.1);
            params.width = (int) (height * 0.65);
            params.height=(int) (height * 0.35);
            params2.width= (int) (width*0.07);
            params2.height= (int) (width*0.07);
        }
        params2.x = params.x;
        params2.y = params.y;
    }
    private void setSmallPlayerView(){
        getDisplaySize();
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        params.x=0;

            if (height>width) {
                params.y= (int) (height*0.2);
                params.width = (int) (width * 0.65);
                params.height=(int) (width * 0.35);

            } else {
                params.y= (int) (height*0.1);
                params.width = (int) (height * 0.65);
                params.height=(int) (height * 0.35);
            }

        params2.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

        updatePlayerViewCloseButton();
        updateWindowManager();
    }

    private void setFullscreenPlayerView() {

        getDisplaySize();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = 0;

        if (isLandscape) {
            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            params2.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        } else if (isReverseLandscape) {

            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            params2.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;

        }

        updateWindowManager();

            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
            params2.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

         updateWindowManager();
        updatePlayerViewCloseButton();
        updateWindowManager();

    }

    private void addViewsToWindowManager(){
        if(playerView!=null) {
            windowManager.addView(playerView, params);
        }
        if(playerViewCloseButton!=null) {
            windowManager.addView(playerViewCloseButton, params2);
        }
    }

    private void removeViewsFromWindowManager() throws IllegalArgumentException{
        try {


            windowManager.removeViewImmediate(playerView);
        }catch (IllegalArgumentException i){
            i.printStackTrace();
        }
            windowManager.removeViewImmediate(playerViewCloseButton);

    }

    private void updateWindowManager()throws IllegalArgumentException{
        if (playerView!=null) {
            try {
                windowManager.updateViewLayout(playerView, params);
            }
            catch (IllegalArgumentException i){
                i.printStackTrace();
            }
            try {
                    windowManager.updateViewLayout(playerViewCloseButton, params2);
                }catch (IllegalArgumentException i) {
                i.printStackTrace();
            }

        }
    }
    private void updatePlayerViewCloseButton() throws IllegalArgumentException {
        getDisplaySize();


                params2.x = params.x;
                params2.y = params.y;




    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {
    if(position<videosId.size()-2)
        position++;
        Intent intent = new Intent(getApplicationContext(), Player.class);
        intent.putExtra("POSITION",position);
        intent.putExtra("VIDEOS_ID", videosId);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
