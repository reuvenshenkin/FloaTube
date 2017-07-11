package com.example.reuven.FloaTube;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by reuven on 25/05/2015.
 */

public class ClearAlertDialogFragment extends android.support.v4.app.DialogFragment{
    public static ClearAlertDialogFragment newInstance() {
        ClearAlertDialogFragment frag = new ClearAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", "Clear Search History");
        args.putString("message", "delete search history?");
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
                        SearchFragment searchFragment = (SearchFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        searchFragment.doPositiveClick();
                    }
                }
        );
        builder.setNegativeButton(getActivity().getString(R.string.alert_dialog_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SearchFragment searchFragment = (SearchFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        searchFragment.doNegativeClick();
                    }
                }
        );

        return builder.create();
    }
}
