package moi.moneytracker.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.android.job.JobManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import moi.moneytracker.BkgJobCreator;
import moi.moneytracker.BkgRecTransactionsJob;
import moi.moneytracker.DatabaseHandler;
import moi.moneytracker.fragments.DatePickerFragment;
import moi.moneytracker.MTApp;
import moi.moneytracker.R;
import moi.moneytracker.models.RecTransaction;
import moi.moneytracker.models.Transaction;
import moi.moneytracker.models.Account;


public class AddTransactionActivity extends AppCompatActivity {


    Toolbar toolbar;

    // Views
    Button transactionDate;
    // Format YYYY-MM-DD
    String formattedDate;

    CheckBox isRecursive;
    Spinner accounts;
    Spinner categories;
    Spinner freqDrop;
    Spinner forDrop;
    EditText amount;
    EditText notes;
    EditText freq;
    EditText end;
    TextView accountLabel;
    TextView footer;

    // Resources
    Intent received;
    boolean isIncome;
    DatabaseHandler db;
    List<Account> accountList;

    String[] unitsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        received = getIntent();
        isIncome = received.getStringExtra("type").equals("1") ? true : false;

        db = MTApp.getDatabase();


        toolbar = (Toolbar) findViewById(R.id.addTransactionTB);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle( isIncome ? R.string.addIncome : R.string.addExpense);


        // Setting Date to today's date.
        transactionDate = (Button) findViewById(R.id.transactionDate);
        Calendar c = Calendar.getInstance();
        ChangeDateText(c.getTime());
        setFormattedDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        Log.d("xyz:",formattedDate);
        transactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        isRecursive = (CheckBox) findViewById(R.id.isRecursiveChk);

        // Disabling Recursive view at start
        isRecursive = (CheckBox) findViewById(R.id.isRecursiveChk);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.recurrsivePanel);
        disableEnableViews(false,layout);

        // Adding listener to Recursive checkbox changes
        isRecursive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                {
                    ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.recurrsivePanel);
                    disableEnableViews(true,layout);
                }
                else
                {
                    ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.recurrsivePanel);
                    disableEnableViews(false,layout);
                }

            }
        });

        // Checking type of transaction(income/expense) to (remove/show) category
        if (isIncome)
        {
            // removing category layout and views
            LinearLayout catLayout = (LinearLayout) findViewById(R.id.categoryLayout);
            RemoveViews(catLayout);

            // moving checkbox up
            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.scrollLayout);
            ConstraintSet constraintSet = new ConstraintSet();
            LinearLayout notesLayout = (LinearLayout) findViewById(R.id.notesLayout);

            constraintSet.clone(constraintLayout);
            constraintSet.connect(isRecursive.getId(), ConstraintSet.TOP, notesLayout.getId(), ConstraintSet.BOTTOM, 100);
            constraintSet.applyTo(constraintLayout);
        }



        // Spinners
        accounts = (Spinner) findViewById(R.id.accountsDrop);
        accountList = db.getAccounts();
        String[] accountDropItems = new String[accountList.size()];
        int i = 0;
        for( Account acc : accountList)
        {
            accountDropItems[i] = acc.getAccountName() + ": " + acc.getBalance() + " " + acc.getCurrency();
            i++;
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item,accountDropItems );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts.setAdapter(adapter);
        accounts.setSelection(0);

        categories = (Spinner) findViewById(R.id.catDrop);
        String[] categoriesArray = getResources().getStringArray(R.array.expenseCategories);
        ArrayAdapter<CharSequence> catAdapter = new ArrayAdapter<CharSequence>(getBaseContext()
                                                , android.R.layout.simple_spinner_item,categoriesArray );
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(catAdapter);
        categories.setSelection(0);

        freqDrop = (Spinner) findViewById(R.id.freqDrop);
        unitsArray = getResources().getStringArray(R.array.recursionUnits);
        ArrayAdapter<CharSequence> unitAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item,unitsArray );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqDrop.setAdapter(unitAdapter);


        forDrop = (Spinner) findViewById(R.id.forDrop);

        freqDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] forUnitsArray = new String[unitsArray.length - i];
                for (int k = 0; k < forUnitsArray.length; k++ )
                {
                    forUnitsArray[k] = unitsArray[k + i];
                }
                ArrayAdapter<CharSequence> forUnitAdapter = new ArrayAdapter<CharSequence>(getBaseContext(), android.R.layout.simple_spinner_item,forUnitsArray );
                forUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                forDrop.setAdapter(forUnitAdapter);
                forDrop.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        freqDrop.setSelection(0);

        amount = (EditText) findViewById(R.id.amount);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String string = editable.toString();

                String[] parts = string.split("\\.");
                if (parts.length > 1) {
                    String digitsAfterPoint = parts[1];
                    if (digitsAfterPoint.length() > 2) {
                        amount.setText(string.substring(0, string.indexOf(".") + 3));
                        amount.setSelection(amount.getText().length());
                    }
                }
            }
        });

        notes = (EditText) findViewById(R.id.notes);
        freq = (EditText) findViewById(R.id.freqNumber);
        end = (EditText) findViewById(R.id.forNumber);

        accountLabel = (TextView) findViewById(R.id.accountLabel);
        accountLabel.setText( isIncome ? R.string.toAccount : R.string.fromAccount);

        footer = (TextView) findViewById(R.id.addTransactionFooter);
        footer.setText(R.string.leaveBlankForInfinite);

    }


    // Recursive method to disable/enable views inside a ViewGroup.
    private void disableEnableViews(boolean enable, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                disableEnableViews(enable, (ViewGroup)child);
            }
        }
    }

    // Recursive method to remove views inside a ViewGroup.
    private void RemoveViews(ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setVisibility(View.GONE);
            if (child instanceof ViewGroup){
                RemoveViews((ViewGroup)child);
            }
        }
    }

    // Set Text Date of the Button
    public void ChangeDateText(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("E , dd - MMM - yyyy");
        String formatted = formatter.format(date);
        transactionDate.setText( formatted );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_tb_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case R.id.cancel:
        {
            finish();
            return true;
        }

        case R.id.add: {
            String amountStr = amount.getText().toString();
            String freqStr = freq.getText().toString();
            String endStr = end.getText().toString();

            double amountDb = amountStr.equals("") ? 0 : Double.valueOf(amountStr);
            int freqNumber = freqStr.equals("") ? 0 : Integer.valueOf(freqStr);
            int endNumber = endStr.equals("") ? -1 : Integer.valueOf(endStr);

            int everySelection = MTApp.getIndexOf(freqDrop.getSelectedItem().toString(),unitsArray);
            int forSelection = MTApp.getIndexOf(forDrop.getSelectedItem().toString(),unitsArray);

            if (amountDb <= 0 || (!isIncome && amountDb > accountList.get(accounts.getSelectedItemPosition()).getBalance())
                    || (isRecursive.isChecked() && freqNumber <= 0 )) {
                // notify that fields are required with valid input
                Toast.makeText(getApplicationContext(), R.string.invalidInputFound, Toast.LENGTH_LONG).show();
                return true;
            }
            else if (endNumber == 0) {
                // notify that fields are required with valid input
                Toast.makeText(this, R.string.cannotBeZero, Toast.LENGTH_LONG).show();
                return true;
            }
            else if ( endNumber != -1 && everySelection == forSelection && freqNumber > endNumber )
            {
                Toast.makeText(this, R.string.recursionLengthsInvalid, Toast.LENGTH_SHORT).show();
                return true;
            }
            Transaction transaction = new Transaction();
            transaction.setRefAccountId(accountList.get(accounts.getSelectedItemPosition()).getAccountId());
            transaction.setType(isIncome);
            transaction.setAmount(amountDb);
            transaction.setDate(formattedDate);
            transaction.setNotes(notes.getText().toString());
            if (isIncome)
                transaction.setCategory("");
            else {
                String[] cats = MTApp.getLocalizedResources(this, new Locale("en")).getStringArray(R.array.expenseCategories);
                int catSelection = categories.getSelectedItemPosition();
                transaction.setCategory(cats[catSelection]);
            }
            db.addTransaction(transaction);

            if (isRecursive.isChecked())
            {
                RecTransaction recTransaction = new RecTransaction();
                recTransaction.setRefTransactionId(db.getLastAddedTransaction().getID());
                recTransaction.setAmount(transaction.getAmount());
                recTransaction.setEveryNum(freqNumber);

                String[] unitsEn = MTApp.getLocalizedResources(this, new Locale("en")).getStringArray(R.array.recursionUnits);
                recTransaction.setEveryUnit(unitsEn[everySelection]);

                if (endNumber != -1 )
                {
                    recTransaction.setForNum(endNumber);
                    recTransaction.setForUnit(unitsEn[forSelection]);
                }

                recTransaction.setLastExDate(transaction.getDate());

                db.addRecTransaction(recTransaction);

                JobManager manager = ((MTApp) getApplication()).getJobManager();
                manager.addJobCreator(new BkgJobCreator(BkgRecTransactionsJob.TAG, getApplicationContext()));

            }


            Toast.makeText(getApplicationContext(), R.string.transactionSaved, Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        default:
            return super.onOptionsItemSelected(item);
    }
}


    public void setFormattedDate( int year, int month, int day)
    {
        String yearStr = year + "";
        while(yearStr.length() != 4)
            yearStr = "0" + yearStr;

        String monthStr = (month + 1) + "";
        while(monthStr.length() != 2)
            monthStr = "0" + monthStr;

        String dayStr = day + "";
        while(dayStr.length() != 2)
            dayStr = "0" + dayStr;

        formattedDate = yearStr + "-" + monthStr + "-" + dayStr;
    }
}
