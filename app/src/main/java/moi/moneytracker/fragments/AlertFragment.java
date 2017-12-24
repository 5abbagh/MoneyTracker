package moi.moneytracker.fragments;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import moi.moneytracker.R;

/**
 * Created by Ali on 05-Nov-17.
 */

public class AlertFragment extends DialogFragment {

    private String message;
    AlertDialogListener mListener;
    Object toDelete;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AlertDialogListener) context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

//    private int accountIndex = -1;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(message);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                mListener.onDialogPositiveClick(AlertFragment.this, toDelete);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogNegativeClick(AlertFragment.this, toDelete);
            }
        });

        return builder.create();
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setToDeleteObject(Object toDel)
    {
        toDelete = toDel;
    }

    public interface AlertDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Object toDelete);
        public void onDialogNegativeClick(DialogFragment dialog, Object toDelete);
    }
}