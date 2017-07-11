package com.example.reuven.FloaTube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.List;


/**
 * Created by reuven on 24/05/2015.
 */
public  class VideoItemsListFragment extends ListFragment{

    private String videoId ;
    private List<VideoItem> searchResults;
    private Handler handler;
    private HistoryDataSource dataSource;
    private ConnectionDetector connectionDetector;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
synchronized (this) {
    handler = new Handler();

}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        connectionDetector=new ConnectionDetector(getActivity().getApplicationContext());
            SearchFragment searchFragment = (SearchFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (connectionDetector.isConnectingToInternet()) {
            searchOnYoutube(searchFragment.getSearchInputText());
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "no internet connection please try again later", Toast.LENGTH_LONG).show();
        }
            searchOnYoutube(searchFragment.getSearchInputText());
            addClickListener();


    }
    private  void searchOnYoutube(final String keywords)throws NullPointerException {
        try {
            new Thread() {
                public void run() {
                    YoutubeConnector yc = new YoutubeConnector(getActivity());
                    searchResults = yc.search(keywords);
                    handler.post(new Runnable() {
                        public synchronized void run() {
                            updateVideosFound();
                        }
                    });
                }
            }.start();


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    private  void updateVideosFound() throws NullPointerException{
        try {


            ArrayAdapter<VideoItem> videoItemArrayAdapter = new ArrayAdapter<VideoItem>(getActivity().getApplicationContext(),
                   R.layout.video_item, searchResults) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {

                        convertView = getLayoutInflater(new Bundle()).inflate(R.layout.video_item, parent, false);
                    }
                    CheckBox checkBox= (CheckBox) convertView.findViewById(R.id.video_item_check_box);
                    ImageButton imageButton= (ImageButton) convertView.findViewById(R.id.imageButton);
                    ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                    TextView title = (TextView) convertView.findViewById(R.id.video_title);
                    //TextView description = (TextView) convertView.findViewById(R.id.video_description);
                   // TextView length = (TextView) convertView.findViewById(R.id.video_description);
                    if (position < searchResults.size()) {
                        VideoItem searchResult = searchResults.get(position);

                        Picasso.with(getActivity().getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                        title.setText(searchResult.getTitle());
                        checkBox.setVisibility(View.GONE);
                        imageButton.setVisibility(View.GONE);
                        // description.setText(searchResult.getDescription());
                        //  length.setText(searchResult.getLength());

                    }


                    return convertView;

                }
            };

            setListAdapter(videoItemArrayAdapter);
            videoItemArrayAdapter.notifyDataSetChanged();
            getListView().setFocusable(true);
            getListView().requestFocus();
            getListView().setClickable(true);
        }catch (NullPointerException n){
            n.printStackTrace();
        }

    }
    private void addClickListener(){
      getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> av, View v, int pos,
                                  long id) {
              // adding video that playing to HistoryDataSource
              dataSource = new HistoryDataSource(getActivity().getApplicationContext());
              try {
                  dataSource.open();
              } catch (SQLException e) {
                  e.printStackTrace();
              }
              VideoItem videoItem=new VideoItem();
              videoItem =searchResults.get(pos);
              dataSource.createRow(videoItem);
              dataSource.close();
              InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.hideSoftInputFromWindow(getListView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
              videoId=searchResults.get(pos).getId();

              Intent intent = new Intent(getActivity().getApplicationContext(), Player.class);
              intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
              intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);

          }

      });
    }

}
