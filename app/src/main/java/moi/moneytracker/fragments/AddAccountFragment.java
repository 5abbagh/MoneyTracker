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
import moi.moneytracker.activities.AccountsActivity;

/**
 * Created by Ali on 05-Nov-17.
 */

public class AddAccountFragment extends DialogFragment
{

    Toolbar toolbar;
    EditText accountName;
    EditText accountCurrency;
    EditText initialBalance;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_add_account, null);
        builder.setView(v);

        toolbar = (Toolbar) v.findViewById(R.id.addMapLocationTB);
        toolbar.setTitle(R.string.addAccount);

        accountName = (EditText) v.findViewById(R.id.locationName);
        accountCurrency = (EditText) v.findViewById(R.id.accountCurrency);
        initialBalance = (EditText) v.findViewById(R.id.accountInitBalance);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
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

                        String name = accountName.getText().toString();
                        String currency = accountCurrency.getText().toString();
                        String balanceStr = initialBalance.getText().toString();

                        double balance = balanceStr.equals("") ? 0 : Double.valueOf(balanceStr);

                        if ( balance < 0 || name.equals("") || currency.equals("") )
                        {
                            // notify that fields are required with valid input
                            Toast.makeText(getActivity(), R.string.invalidInputFound, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            dialog.dismiss();
                            AccountsActivity accountsActivity = (AccountsActivity) getActivity();
                            accountsActivity.addAccount(name,currency,balance);
                            // display transaction added
                            Toast.makeText(getActivity(), R.string.accountAdded, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return dialog;
    }

}
