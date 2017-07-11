package com.example.reuven.FloaTube;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by reuven on 13/09/2015.
 */
public class InternetConnectionAlertDialogFragment extends android.support.v4.app.DialogFragment{
    public static InternetConnectionAlertDialogFragment newInstance() {
        InternetConnectionAlertDialogFragment frag = new InternetConnectionAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", "No Internet Connection");
        args.putString("message", "please connect to the internet and try again!");
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message =getArguments().getString("message");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setIcon(R.mipmap.floatube_logo);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(getActivity().getString(R.string.alert_dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }
        );

        return builder.create();
    }
}
