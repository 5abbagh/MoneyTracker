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

import moi.moneytracker.models.MapLocation;
import moi.moneytracker.activities.MapsActivity;
import moi.moneytracker.R;

/**
 * Created by Ali on 15-Nov-17.
 */

public class AddMapLocationFragment extends DialogFragment
{
    Toolbar toolbar;
    EditText locationName;
    private double lat;
    private double lng;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_add_maplocation, null);
        builder.setView(v);

        toolbar = (Toolbar) v.findViewById(R.id.addMapLocationTB);
        toolbar.setTitle(R.string.addMapLocation);

        locationName = (EditText) v.findViewById(R.id.locationName);

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

                        String name = locationName.getText().toString();

                        if ( name.equals("") )
                        {
                            // notify that fields are required with valid input
                            Toast.makeText(getActivity(), R.string.locationNameCannotBeEmpty, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            dialog.dismiss();
                            MapLocation location = new MapLocation(name,lat,lng);
                            MapsActivity mapsActivity = (MapsActivity) getActivity();
                            mapsActivity.addMapLocation(location);
                            // display transaction added
                            Toast.makeText(getActivity(), R.string.locationAdded, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public void setLng(double lng)
    {
        this.lng = lng;
    }

}
