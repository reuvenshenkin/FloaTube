package com.example.reuven.FloaTube;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by reuven on 23/05/2015.
 */
public class UserSearchListFragment extends ListFragment implements AdapterView.OnItemClickListener{

    private UserSearchDataSource datasource;
    List<UserSearch> values;
    ArrayAdapter<UserSearch> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        datasource = new UserSearchDataSource(getActivity().getApplicationContext());
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        values = datasource.getAllUserSearch();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
         adapter = new ArrayAdapter<UserSearch>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        getListView().setDividerHeight(5);



    }




    @Override
    public void onResume() {
        super.onResume();
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        datasource.close();
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchFragment searchFragment= (SearchFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        searchFragment.setSearchInputText(parent.getAdapter().getItem(position).toString());
//        AutoCompleteTextView searchInput= (AutoCompleteTextView) getActivity().findViewById(R.id.search_input);
//        searchInput.setText(values.get(position).toString());
    }
}
