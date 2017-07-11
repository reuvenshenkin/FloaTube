package com.example.reuven.FloaTube;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private SearchFragment searchFragment;
    private HistoryFragment historyFragment;
    private FavoritesFragment favoritesFragment;
    private Button  search, favorites, history;
    private TextView fragmentsTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            searchFragment = new SearchFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, searchFragment);
            fragmentTransaction.commit();
        }
            search = (Button) findViewById(R.id.search_fragment_button);
            favorites = (Button) findViewById(R.id.favorites_fragment_button);
            history = (Button) findViewById(R.id.history_fragment_button);
            fragmentsTitle= (TextView) findViewById( R.id.fragmets_title);
        addOnItemClickListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container)instanceof HistoryFragment) {
            fragmentsTitle.setText("history");
            search.setBackgroundColor(Color.BLACK);
            search.setTextColor(Color.RED);
            history.setBackgroundColor(Color.RED);
            history.setTextColor(Color.BLACK);
            favorites.setBackgroundColor(Color.BLACK);
            favorites.setTextColor(Color.RED);

        }
        else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container)instanceof FavoritesFragment){
            fragmentsTitle.setText("favorites");

            search.setBackgroundColor(Color.BLACK);
            search.setTextColor(Color.RED);
            history.setBackgroundColor(Color.BLACK);
            history.setTextColor(Color.RED);
            favorites.setBackgroundColor(Color.RED);
            favorites.setTextColor(Color.BLACK);
        }
        else{
            fragmentsTitle.setText("search");

            search.setBackgroundColor(Color.RED);
            search.setTextColor(Color.BLACK);
            history.setBackgroundColor(Color.BLACK);
            history.setTextColor(Color.RED);
            favorites.setBackgroundColor(Color.BLACK);
            favorites.setTextColor(Color.RED);
        }

    }

    @Override
    protected void onDestroy() throws IllegalArgumentException {
        try {
            super.onDestroy();
        } catch (IllegalArgumentException i) {
            i.printStackTrace();
        }
    }
    private void addOnItemClickListener(){

        search.setOnClickListener(this);
        history.setOnClickListener(this);
        favorites.setOnClickListener(this);
}
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_fragment_button:
                fragmentsTitle.setText("search");

                searchFragment = new SearchFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, searchFragment);
                fragmentTransaction.commit();
                search.setBackgroundColor(Color.RED);
                search.setTextColor(Color.BLACK);
                history.setBackgroundColor(Color.BLACK);
                history.setTextColor(Color.RED);
                favorites.setBackgroundColor(Color.BLACK);
                favorites.setTextColor(Color.RED);
                break;

            case R.id.history_fragment_button:
                fragmentsTitle.setText("history");

                historyFragment = new HistoryFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, historyFragment);
                fragmentTransaction.commit();
                search.setBackgroundColor(Color.BLACK);
                search.setTextColor(Color.RED);
                history.setBackgroundColor(Color.RED);
                history.setTextColor(Color.BLACK);
                favorites.setBackgroundColor(Color.BLACK);
                favorites.setTextColor(Color.RED);
                break;

            case R.id.favorites_fragment_button:
                fragmentsTitle.setText("favorites");

                favoritesFragment = new FavoritesFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, favoritesFragment);
                fragmentTransaction.commit();
                search.setBackgroundColor(Color.BLACK);
                search.setTextColor(Color.RED);
                history.setBackgroundColor(Color.BLACK);
                history.setTextColor(Color.RED);
                favorites.setBackgroundColor(Color.RED);
                favorites.setTextColor(Color.BLACK);
                break;
        }
    }
}
