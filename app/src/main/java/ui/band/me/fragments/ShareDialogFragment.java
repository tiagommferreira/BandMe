package ui.band.me.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Timer;

import ui.band.me.API.TwitterAPI;
import ui.band.me.R;

/**
 * Created by Tiago on 19-05-2015.
 */
public class ShareDialogFragment extends DialogFragment {

    private String bandName;

    public static ShareDialogFragment newInstance(String band) {
        ShareDialogFragment f = new ShareDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("bandName", band);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_share, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v);

        final EditText tweetText = (EditText) v.findViewById(R.id.shareMessage);
        tweetText.setText("I just found " + getArguments().getString("bandName") + " with BandMe  @rjrloureiro @Asura_14");


        builder.setPositiveButton("Tweet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //posts tweet
                new TwitterAPI(tweetText.getText().toString()).execute();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ShareDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle("Write your tweet");


        return builder.create();
    }


}
