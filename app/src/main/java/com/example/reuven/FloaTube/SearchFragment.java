package com.example.reuven.FloaTube;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by reuven on 16/05/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SearchFragment extends Fragment implements View.OnClickListener {
    private Button clear,history;
    private AutoCompleteTextView searchInput;
    private ImageButton search;
    private Handler handler;
    private UserSearchListFragment userSearchListFragment;
    private VideoItemsListFragment videoItemsListFragment;
    private ArrayAdapter<String> completioItemArrayAdapter;
    private UserSearchDataSource datasource;
    private ArrayAdapter<UserSearch> adapter;
    private ConnectionDetector connectionDetector;
    private int counter=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState==null) {
            userSearchListFragment = new UserSearchListFragment();
            android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.list_fragment_container, userSearchListFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else
            counter=savedInstanceState.getInt("counter");
    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        clear= (Button) view.findViewById(R.id.clear_button);
        clear.setOnClickListener(this);
        history= (Button) view.findViewById(R.id.search_history_button);
        history.setOnClickListener(this);
        if (counter==0) {
            history.setVisibility(View.GONE);
        }
        else {
            clear.setVisibility(View.GONE);
        }

        search= (ImageButton) view.findViewById(R.id.search_button);
        search.setOnClickListener(this);
        searchInput= (AutoCompleteTextView) view.findViewById(R.id.search_input);

        searchInput.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) throws ExceptionInInitializerError {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    connectionDetector = new ConnectionDetector(getActivity().getApplicationContext());
                    if (connectionDetector.isConnectingToInternet()) {
                        searchInput.dismissDropDown();
                        counter++;
                        history.setVisibility(View.VISIBLE);
                        clear.setVisibility(View.GONE);
                        videoItemsListFragment = new VideoItemsListFragment();
                        android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.list_fragment_container, videoItemsListFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                    else {
                        showInternetConnectionAlert();
                    }


                    datasource = new UserSearchDataSource(getActivity().getApplicationContext());
                    try {
                        datasource.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    UserSearch userSearch = datasource.createUserSearch(searchInput.getText().toString());
                    datasource.close();
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_NONE) {

                }
                return true;
            }
        });
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                connectionDetector = new ConnectionDetector(getActivity().getApplicationContext());
                if (connectionDetector.isConnectingToInternet()) {
                    autoCompleteFromYoutube(s.toString());
                }
                else {
                    showInternetConnectionAlert();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //autoCompleteFromYoutube(searchInput.getText().toString());

            }
        });

        handler=new Handler();

        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", counter);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    public void setSearchInputText(String text) {
        searchInput.setText(text);
    }
    public String getSearchInputText(){
        return searchInput.getText().toString();
    }
    private List<String> autoCompleteResults;
    private void autoCompleteFromYoutube(final String keys){
        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector(getActivity());
                autoCompleteResults = yc.autoComplete(keys);
                searchInput.post(new Runnable(){
                    public void run(){
                        updateAutoComplete();
                    }
                });
            }
        }.start();
    }
    private void updateAutoComplete(){
            completioItemArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, autoCompleteResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater(new Bundle()).inflate(R.layout.auto_complete_item, parent, false);

                }
                if (position<autoCompleteResults.size()) {
                    TextView title = (TextView) convertView.findViewById(R.id.auto_complete_item);
                    String autoCompleteResult = autoCompleteResults.get(position);
                    title.setText(autoCompleteResult.toString() + "\n");
                }
                    return convertView;

            }
        };


    searchInput.setAdapter(completioItemArrayAdapter);
        completioItemArrayAdapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View v) {

         if (v == getView().findViewById(R.id.clear_button)){
             showClearAlertDialog();
//
        }
         else if (v == getView().findViewById(R.id.search_history_button) ){
             clear.setVisibility(View.VISIBLE);
             history.setVisibility(View.GONE);

             userSearchListFragment=new UserSearchListFragment();
             android.support.v4.app.FragmentManager fragmentManager =getChildFragmentManager();
             android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
             fragmentTransaction.replace(R.id.list_fragment_container, userSearchListFragment);
             fragmentTransaction.addToBackStack(null);
             fragmentTransaction.commit();

        }


        else if (v == getView().findViewById(R.id.search_button)) {
             InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(searchInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


             try {
                 if (connectionDetector.isConnectingToInternet()) {
                     counter++;
                     clear.setVisibility(View.GONE);
                     history.setVisibility(View.VISIBLE);
                     videoItemsListFragment = new VideoItemsListFragment();
                     android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
                     android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                     fragmentTransaction.replace(R.id.list_fragment_container, videoItemsListFragment);
                     fragmentTransaction.addToBackStack(null);
                     fragmentTransaction.commit();
                 }
                 else {
                    showInternetConnectionAlert();
                 }

             }catch (NullPointerException e){
                 e.printStackTrace();
             }


            datasource = new UserSearchDataSource(getActivity().getApplicationContext());
            try {
                datasource.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            UserSearch userSearch = datasource.createUserSearch(searchInput.getText().toString());
            datasource.close();

        }
    }
    private void showClearAlertDialog() {
        DialogFragment newFragment = ClearAlertDialogFragment.newInstance();
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
    private void showInternetConnectionAlert(){
        DialogFragment newFragment = InternetConnectionAlertDialogFragment.newInstance();
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
        datasource = new UserSearchDataSource(getActivity().getApplicationContext());
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        datasource.deleteAllUserSearch();
        userSearchListFragment = (UserSearchListFragment) getChildFragmentManager().findFragmentById(R.id.list_fragment_container);
        List<UserSearch> values = datasource.getAllUserSearch();
        adapter = new ArrayAdapter<UserSearch>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, values);
        userSearchListFragment.setListAdapter(adapter);
        datasource.close();
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }
}






