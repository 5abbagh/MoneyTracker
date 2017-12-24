package moi.moneytracker.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import moi.moneytracker.MTApp;
import moi.moneytracker.R;
import moi.moneytracker.activities.RecTransactionsActivity;
import moi.moneytracker.models.RecTransaction;

/**
 * Created by Ali on 02-Dec-17.
 */

public class EditRecTransactionFragment extends DialogFragment
{
    Toolbar toolbar;
    EditText recAmount;
    TextView fromAcnt;
    TextView catNotes;
    EditText freqNumber;
    EditText endNumber;
    Spinner freqUnits;
    Spinner endUnits;
    TextView footer;

    ///////
    String fromAcntStr;
    String catNotesStr;

    String[] unitsArray;
    String[] unitsArrayEn;
    RecTransaction toEdit;
    String originalDate;


    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_edit_rectransaction, null);
        builder.setView(v);

        toolbar = (Toolbar) v.findViewById(R.id.editRecTransactionTB);
        toolbar.setTitle(R.string.editRecTransaction);
        recAmount = (EditText) v.findViewById(R.id.amount);
        recAmount.setText(toEdit.getAmount() + "");
        freqNumber = (EditText) v.findViewById(R.id.freqNumber);
        freqNumber.setText(toEdit.getEveryNum() + "");
        endNumber = (EditText) v.findViewById(R.id.forNumber);
        if ( toEdit.getForNum() > 0 )
            endNumber.setText(toEdit.getForNum() + "");
        fromAcnt = (TextView) v.findViewById(R.id.accountLabel);
        fromAcnt.setText(fromAcntStr);
        catNotes = (TextView) v.findViewById(R.id.catNotesLbl);
        catNotes.setText(catNotesStr);
        footer = (TextView) v.findViewById(R.id.editRecTransactionFooter);
        footer.setText(getString(R.string.leaveBlankForInfinite) + "\n" + getString(R.string.forFieldFromOriginal) + " " + originalDate);

        unitsArray = getResources().getStringArray(R.array.recursionUnits);
        unitsArrayEn = MTApp.getLocalizedResources(getContext(),new Locale("en")).getStringArray(R.array.recursionUnits);

        ArrayAdapter<CharSequence> unitAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item,unitsArray );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        freqUnits = (Spinner) v.findViewById(R.id.freqDrop);
        freqUnits.setAdapter(unitAdapter);


        endUnits = (Spinner) v.findViewById(R.id.forDrop);

        freqUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] forUnitsArray = new String[unitsArray.length - i];
                String[] forUnitsArrayEn = new String[unitsArray.length - i];
                for (int k = 0; k < forUnitsArray.length; k++ )
                {
                    forUnitsArray[k] = unitsArray[k + i];
                    forUnitsArrayEn[k] = unitsArrayEn[k + i];
                }
                ArrayAdapter<CharSequence> forUnitAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item,forUnitsArray );
                forUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                endUnits.setAdapter(forUnitAdapter);
                endUnits.setSelection(MTApp.getIndexOf(toEdit.getForUnit(),forUnitsArrayEn));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        freqUnits.setSelection(MTApp.getIndexOf(toEdit.getEveryUnit(),unitsArrayEn));

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

                        String amountStr = recAmount.getText().toString();
                        String everyStr = freqNumber.getText().toString();
                        String forStr = endNumber.getText().toString();

                        double amount = amountStr.equals("") ? 0 : Double.valueOf(amountStr);
                        int everyNumber =  everyStr.equals("") ? 0 : Integer.valueOf(everyStr);
                        int forNumber =  forStr.equals("") ? -1 : Integer.valueOf(forStr);

                        int everySelection = MTApp.getIndexOf(freqUnits.getSelectedItem().toString(),unitsArray);
                        int forSelection = MTApp.getIndexOf(endUnits.getSelectedItem().toString(),unitsArray);

                        if ( amount <= 0 || everyNumber <= 0 )
                        {
                            // notify that fields are required with valid input
                            Toast.makeText(getActivity(), R.string.invalidInputFound, Toast.LENGTH_SHORT).show();
                        }
                        else if ( forNumber == 0 )
                        {
                            // notify that fields are required with valid input
                            Toast.makeText(getActivity(), R.string.cannotBeZero, Toast.LENGTH_LONG).show();
                        }
                        else if ( forNumber != -1 && everySelection == forSelection && everyNumber > forNumber )
                        {
                            Toast.makeText(getContext(), R.string.recursionLengthsInvalid, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            String[] unitsEn = MTApp.getLocalizedResources(getContext(), new Locale("en")).getStringArray(R.array.recursionUnits);

                            toEdit.setAmount(amount);
                            toEdit.setEveryNum(everyNumber);
                            toEdit.setEveryUnit(unitsEn[everySelection]);

                            if (forNumber != -1  )
                            {
                                toEdit.setForNum(forNumber);
                                toEdit.setForUnit(unitsEn[forSelection]);
                                Log.d("xyz:","in != -1");
                            }
                            if ( forNumber == -1 && toEdit.getForNum() != 0 )
                            {
                                toEdit.setForNum(0);
                                toEdit.setForUnit("");
                                Log.d("xyz:","in == -1 && != 0 ");
                            }

                            RecTransactionsActivity recTransactionsActivity = (RecTransactionsActivity) getActivity();
                            recTransactionsActivity.editRecTransaction(toEdit);
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.recTransactionEdited, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    public void setFromAcnt(String fromAcnt )
    {
        fromAcntStr = fromAcnt;
    }

    public void setCatNotes(String catNotes )
    {
        catNotesStr = catNotes;
    }

    public void setToEditObject( RecTransaction te ){ toEdit = te; }

    public void setOriginalDate( String date ){ originalDate = date; }

}
