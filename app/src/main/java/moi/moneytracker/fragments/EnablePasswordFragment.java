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

public class EnablePasswordFragment extends DialogFragment {

    Toolbar toolbar;
    EditText password;
    boolean enabling;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_enable_password, null);
        builder.setView(v);

        toolbar = (Toolbar) v.findViewById(R.id.enablePassTB);
        toolbar.setTitle( (enabling ? R.string.enablePassword : R.string.disablePassword) );

        password = (EditText) v.findViewById(R.id.enterPass);

        builder.setPositiveButton( (enabling ? R.string.enable : R.string.disable), new DialogInterface.OnClickListener() {
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

                        String pass = password.getText().toString();
                        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
                        if ( !settingsActivity.checkDbPass(pass) )
                        {
                            // notify that fields are required with valid input
                            Toast.makeText(getActivity(), R.string.passwordIsWrong, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            settingsActivity.enableDisablePassword( enabling );
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    public void setEnabling (boolean enable){ enabling = enable; }
}
