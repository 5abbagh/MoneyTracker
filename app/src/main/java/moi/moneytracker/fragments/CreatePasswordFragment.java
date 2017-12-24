package moi.moneytracker.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import moi.moneytracker.R;
import moi.moneytracker.activities.SettingsActivity;

/**
 * Created by Ali on 30-Nov-17.
 */

public class CreatePasswordFragment extends DialogFragment {

    Toolbar toolbar;
    EditText password;
    EditText confirmPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_create_password, null);
        builder.setView(v);

        toolbar = (Toolbar) v.findViewById(R.id.createPassTB);
        toolbar.setTitle(R.string.setPassword);

        password = (EditText) v.findViewById(R.id.password);
        confirmPassword = (EditText) v.findViewById(R.id.confirmPassword);

        builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String newPass = password.getText().toString();
                        String confirm = confirmPassword.getText().toString();
                        SettingsActivity settingsActivity = (SettingsActivity) getActivity();

                        if ( newPass.equals("") || confirm.equals("") )
                        {
                            // notify that fields are required with valid input
                            Toast.makeText(getActivity(), R.string.invalidInputFound, Toast.LENGTH_SHORT).show();
                        }
                        else if ( ! newPass.equals(confirm) )
                        {
                            Toast.makeText(getActivity(), R.string.noMatch, Toast.LENGTH_SHORT).show();
                        }
                        else if ( newPass.length() < 5 || confirm.length() < 5 )
                        {
                            Toast.makeText(getActivity(), R.string.shortPassword, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            settingsActivity.setPassword("",newPass);
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.passwordSet, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return dialog;
    }
}
