package com.example.reuven.FloaTube;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reuven on 13/07/2015.
 */
public class FavoritesFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{
    private FavoritesDataSource datasource;
    List<VideoItem> values;
    ArrayAdapter<VideoItem> adapter;
    private Button edit,delete,selectAll;
    private ListView listView;
    private boolean isFragmentFirstTimeCreated=true,isDeleteEnabled,isCheckboxesVisible;
    private String etitButtonText,deleteButtonText,selcectAllButtonText;
    private int deleteButtonVisibility,selcectAllButtonVisibility,clickedItemPosition;
    private boolean[] isItemsChecked;
    private ArrayList<String> videosId;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.favorites_fragment, container, false);
        edit = (Button) view.findViewById(R.id.edit_button);
        delete = (Button) view.findViewById(R.id.delete_button1);
        selectAll = (Button) view.findViewById(R.id.select_all_button1);
        listView = (ListView) view.findViewById(R.id.listView1);


        setButtonsVisibility(false);
        setButtonsOnClick();


        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        values = datasource.getAllRows();
        datasource.close();
        if (values.size()==0){
            edit.setVisibility(View.GONE);
        }
        if (!isFragmentFirstTimeCreated){
            edit.setText(etitButtonText);
            delete.setText(deleteButtonText);
            selectAll.setText(selcectAllButtonText);
            delete.setVisibility(deleteButtonVisibility);
            selectAll.setVisibility(selcectAllButtonVisibility);
            delete.setEnabled(isDeleteEnabled);
            if (clickedItemPosition>=0){
                values.get(clickedItemPosition).setIsClicked(true);
            }
            for (int i=0;i<values.size();i++){
                values.get(i).setIsCheckBoxVisible(isCheckboxesVisible);
                values.get(i).setIsChecked(isItemsChecked[i]);
            }
        }
        // use the SimpleCursorAdapter to show the
        // elements in a ListView

        try {


            adapter = new ArrayAdapter<VideoItem>(getActivity().getApplicationContext(),
                    R.layout.video_item, values) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {

                        convertView = getLayoutInflater(new Bundle()).inflate(R.layout.video_item, parent, false);
                    }
                    CheckBox cb = (CheckBox) convertView.findViewById(R.id.video_item_check_box);

                    ImageButton menuButton = (ImageButton) convertView.findViewById(R.id.imageButton);
                    ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                    TextView title = (TextView) convertView.findViewById(R.id.video_title);
                    //TextView description = (TextView) convertView.findViewById(R.id.video_description);
                    // TextView length = (TextView) convertView.findViewById(R.id.video_description);
                    if (position < values.size()) {
                        final VideoItem value = values.get(position);

                        Picasso.with(getActivity().getApplicationContext()).load(value.getThumbnailURL()).into(thumbnail);
                        title.setText(value.getTitle());
                        // description.setText(searchResult.getDescription());
                        //  length.setText(searchResult.getLength());
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                value.setIsChecked(isChecked);
                                if (numOfCheckBoxesChecked()>0 && numOfCheckBoxesChecked()<values.size()){
                                    delete.setEnabled(true);
                                    delete.setText("DELETE (" + numOfCheckBoxesChecked() + ")");
                                    selectAll.setText("SELECT ALL");

                                }
                                else if (numOfCheckBoxesChecked()==0){
                                    delete.setEnabled(false);
                                    delete.setText("DELETE");
                                    selectAll.setText("SELECT ALL");
                                }
                                else{
                                    delete.setEnabled(true);
                                    delete.setText("DELETE (" + numOfCheckBoxesChecked() + ")");
                                    selectAll.setText("UNSELECT ALL");
                                }
                            }
                        });
                        if (edit.getText().equals("DONE")){
                            value.setIsCheckBoxVisible(true);
                        }
                        cb.setChecked(value.isChecked());
                        if (value.isCheckBoxVisible()) {
                            cb.setVisibility(View.VISIBLE);
                        } else {
                            cb.setVisibility(View.GONE);
                        }
                        if (value.isClicked()) {
                            convertView.setBackgroundColor(Color.RED);
                        } else {
                            convertView.setBackgroundColor(Color.BLACK);
                        }

                        setOnMenuButtonClickListener(menuButton, position);
                    }


                    return convertView;

                }
            };
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(this);
            listView.setDividerHeight(3);
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        return view;

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datasource = new FavoritesDataSource(getActivity().getApplicationContext());
        if(savedInstanceState!=null) {
            isFragmentFirstTimeCreated=false;
            clickedItemPosition= savedInstanceState.getInt("clickedItemPosition");
            isCheckboxesVisible=savedInstanceState.getBoolean("isCheckboxesVisible");
            isItemsChecked=savedInstanceState.getBooleanArray("isItemsChecked");
            etitButtonText=savedInstanceState.getString("etitButtonText");
            deleteButtonText= savedInstanceState.getString("deleteButtonText");
            selcectAllButtonText=savedInstanceState.getString("selectAllButtonText");
            deleteButtonVisibility=savedInstanceState.getInt("deleteButtonVisibility");
            selcectAllButtonVisibility=savedInstanceState.getInt("selectAllButtonVisibility");
            isDeleteEnabled=savedInstanceState.getBoolean("isDeleteEnabled");
            isItemsChecked=savedInstanceState.getBooleanArray("isItemsChecked");


        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isItemsChecked=new boolean[values.size()];
        clickedItemPosition=-1;
        for (int i=0;i<values.size();i++){
            if (values.get(i).isClicked()){
                clickedItemPosition=i;
            }
            if (values.get(i).isCheckBoxVisible()){
                isCheckboxesVisible=true;
            }
            isItemsChecked[i] = values.get(i).isChecked();
        }
        outState.putInt("clickedItemPosition", clickedItemPosition);
        outState.putBoolean("isCheckboxesVisible", isCheckboxesVisible);
        outState.putBooleanArray("isItemsChecked", isItemsChecked);

        outState.putString("etitButtonText", edit.getText().toString());
        outState.putString("deleteButtonText", delete.getText().toString());
        outState.putString("selectAllButtonText", selectAll.getText().toString());
        outState.putInt("deleteButtonVisibility", delete.getVisibility());
        outState.putInt("selectAllButtonVisibility", selectAll.getVisibility());
        outState.putBoolean("isDeleteEnabled", delete.isEnabled());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onStart() {
        super.onStart();



    }











    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i =0;i<values.size();i++){
            if (i==position){
                values.get(i).setIsClicked(true);
            }
            else{
                values.get(i).setIsClicked(false);
            }

        }
        adapter.notifyDataSetChanged();
        videosId=new ArrayList<String>(values.size());
        for (VideoItem item:values){
            videosId.add(item.getId());
        }

        Intent intent = new Intent(getActivity().getApplicationContext(), Player.class);
        intent.putExtra("POSITION",position);
        intent.putExtra("VIDEOS_ID", videosId);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_button:
                if (edit.getText().equals("EDIT")) {
                    edit.setText("DONE");
                    delete.setEnabled(false);
                    setButtonsVisibility(true);
                    for (VideoItem item : values) {
                        item.setIsCheckBoxVisible(true);
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    edit.setText("EDIT");
                    delete.setText("DELETE");
                    setButtonsVisibility(false);
                    for (VideoItem item : values) {
                        item.setIsCheckBoxVisible(false);
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.delete_button1:
               FavoritesDataSource favoritesDataSource=new FavoritesDataSource(getActivity().getApplicationContext());
                try {
                    favoritesDataSource.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                for (int i=0;i<values.size();i++){

                    if (values.get(i).isChecked()){

                        favoritesDataSource.deleteRow(values.get(i));
                        values.remove(i);
                        i--;

                    }
                }
                favoritesDataSource.close();
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                delete.setText("DELETE");
                delete.setEnabled(false);
                if (values.size()==0) {
                    edit.setVisibility(View.GONE);
                    setButtonsVisibility(false);
                }
                break;
            case R.id.select_all_button1:
                if (selectAll.getText().equals("SELECT ALL")) {
                    selectAll.setText("UNSELECT ALL");
                    delete.setEnabled(true);
                    delete.setText("DELETE (" + values.size() + ")");
                    for (VideoItem item : values){
                        item.setIsChecked(true);
                    }
                    adapter.notifyDataSetChanged();

                }
                else{
                    selectAll.setText("SELECT ALL");
                    delete.setEnabled(false);
                    delete.setText("DELETE");
                    for (VideoItem item : values){
                        item.setIsChecked(false);
                    }
                    adapter.notifyDataSetChanged();

                }
                break;
        }

    }
    private void setButtonsOnClick(){
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        selectAll.setOnClickListener(this);

    }
    private void setButtonsVisibility(boolean b){
        if(b){
            delete.setVisibility(View.VISIBLE);
            selectAll.setVisibility(View.VISIBLE);

        }
        else{
            delete.setVisibility(View.GONE);
            selectAll.setVisibility(View.GONE);
        }
    }
    private int numOfCheckBoxesChecked(){
        int count=0;
        for (VideoItem item:values){
            if (item.isChecked()){
                count++;
            }
        }
        return count;
    }
    private void setOnMenuButtonClickListener(final ImageButton menuButton , final int position){
        final HistoryDataSource historyDataSource =new HistoryDataSource(getActivity().getApplicationContext());
        final FavoritesDataSource favoritesDataSource=new FavoritesDataSource(getActivity().getApplicationContext());
        menuButton.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(),menuButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.favorites_fragment_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.item_delete:
                                try {
                                    historyDataSource.open();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                historyDataSource.deleteRow(values.get(position));
                                adapter.remove(values.get(position));
                                values=historyDataSource.getAllRows();

                                historyDataSource.close();

                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }


        });//closing the setOnClickListener method

    }
}
