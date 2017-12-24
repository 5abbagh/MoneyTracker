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

public class EditPasswordFragment extends DialogFragment {


    Toolbar toolbar;
    EditText oldPassword;
    EditText newPassword;
    EditText confirmPass;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_edit_password, null);
        builder.setView(v);

        toolbar = (Toolbar) v.findViewById(R.id.editPassTB);
        toolbar.setTitle(R.string.editPassword);

        oldPassword = (EditText) v.findViewById(R.id.oldPassword);
        newPassword = (EditText) v.findViewById(R.id.newPassword);
        confirmPass = (EditText) v.findViewById(R.id.confirmPass);

        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
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

                        String oldPass = oldPassword.getText().toString();
                        String newPass = newPassword.getText().toString();
                        String confirm = confirmPass.getText().toString();
                        SettingsActivity settingsActivity = (SettingsActivity) getActivity();

                        if ( oldPass.equals("") || newPass.equals("") || confirm.equals("") )
                        {
                            // notify that fields are required with valid input
                            Toast.makeText(getActivity(), R.string.invalidInputFound, Toast.LENGTH_SHORT).show();
                        }
                        else if ( !settingsActivity.checkDbPass(oldPass) )
                        {
                            Toast.makeText(getActivity(), R.string.oldPassWrong, Toast.LENGTH_SHORT).show();
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
                            settingsActivity.changePassword(oldPass,newPass);
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
