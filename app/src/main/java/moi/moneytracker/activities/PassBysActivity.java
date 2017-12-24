package moi.moneytracker.activities;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import moi.moneytracker.DatabaseHandler;
import moi.moneytracker.MTApp;
import moi.moneytracker.R;
import moi.moneytracker.models.Transaction;
import moi.moneytracker.models.Account;
import moi.moneytracker.models.PassBy;

public class PassBysActivity extends AppCompatActivity {


    private DatabaseHandler db;
    private ArrayList<PassBy> passes;
    private LinearLayout allPassBysLayout;
    private PassBysCreateAdapter createAdapter;
    private Toolbar toolbar;
    ArrayList<Account> accounts;
    String[] accountDropItems;
    ArrayAdapter<CharSequence> accAdapter;
    String[] categoriesArray;
    ArrayAdapter<CharSequence> catAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_bys);


        toolbar = (Toolbar) findViewById(R.id.passBysTB);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle( R.string.recentPassBys );

        createAdapter = new PassBysCreateAdapter();

        allPassBysLayout = (LinearLayout) findViewById(R.id.allPassBysLayout);

        db = MTApp.getDatabase();
        passes = (ArrayList<PassBy>) db.getPassBys();


        accounts = (ArrayList<Account>) db.getAccounts();
        accountDropItems = new String[accounts.size()];
        int index = 0;
        for( Account acc : accounts)
        {
            accountDropItems[index] = acc.getAccountName() + ": " + acc.getBalance() + " " + acc.getCurrency();
            index++;
        }
        accAdapter = new ArrayAdapter<CharSequence>(getBaseContext()
                , android.R.layout.simple_spinner_item,accountDropItems );
        accAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        categoriesArray = getResources().getStringArray(R.array.expenseCategories);
        catAdapter = new ArrayAdapter<CharSequence>(getBaseContext()
                , android.R.layout.simple_spinner_item,categoriesArray );
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);





        for (int i = 0; i < passes.size(); i++ )
        {
            View view = createAdapter.getView(i,null,allPassBysLayout);
            allPassBysLayout.addView(view);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.passbys_tb_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
            {
                db.deleteAllPassBys();
                finish();
                return true;
            }

            case R.id.add:
            {
                int size = allPassBysLayout.getChildCount();
                for ( int i = 0; i < size; i++ )
                {
                    View view = allPassBysLayout.getChildAt(i);
                    LinearLayout passItemHeader = view.findViewById(R.id.passItemHeader);

                    String [] cats = MTApp.getLocalizedResources(this, new Locale("en")).getStringArray(R.array.expenseCategories);

                    if (passItemHeader.getAlpha() == 1)
                    {
                        String amountStr = ((TextView)view.findViewById(R.id.amount)).getText().toString();
                        double amount = amountStr.equals("") ? 0 : Double.valueOf(amountStr);
                        if (amount > 0)
                        {
                            int selectedAccountPos = ((Spinner)view.findViewById(R.id.accountsDrop)).getSelectedItemPosition();
                            int refAccount = accounts.get(selectedAccountPos).getAccountId();
                            String passDate = passes.get(i).getPassDate();
                            String dateFormatted = Calendar.getInstance().get(Calendar.YEAR) + "-" + passDate.substring(0,2) + "-" + passDate.substring(3,5);
                            String notes = ((TextView)view.findViewById(R.id.notes)).getText().toString();
                            int catSelection = ((Spinner)view.findViewById(R.id.catDrop)).getSelectedItemPosition();

                            Transaction transaction = new Transaction();
                            transaction.setRefAccountId(refAccount);
                            transaction.setType(false);
                            transaction.setAmount(amount);
                            transaction.setDate(dateFormatted);
                            transaction.setCategory(cats[catSelection]);
                            transaction.setNotes(notes);

                            db.addTransaction(transaction);
                        }
                    }
                }
                db.deleteAllPassBys();
                Toast.makeText(getApplicationContext(), R.string.selTransAdded, Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeShowViews(boolean show, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setVisibility(show ? View.VISIBLE : View.GONE);
            if (child instanceof ViewGroup){
                removeShowViews(show, (ViewGroup)child);
            }
        }
    }

    private class PassBysCreateAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return passes.size();
        }

        @Override
        public String getItem(int position) {
            return "";
        }

        @Override
        public long getItemId(int position) {
            return passes.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container)
        {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.pass_item, container, false);
            }

            final PassBy pass = passes.get(position) ;

            TextView locationDate = (TextView) convertView.findViewById(R.id.locationDate);
            locationDate.setText(db.getMapLocation(pass.getRefMapLocation()).getName() + " " + getString(R.string.at) + " " + pass.getPassDate());

            final ConstraintLayout passItemBody = convertView.findViewById(R.id.passItemBody);
            final LinearLayout passItemHeader = convertView.findViewById(R.id.passItemHeader);

            final CheckBox addCheck = convertView.findViewById(R.id.addCheckBox);
            addCheck.setChecked(false);
            addCheck.setClickable(false);
            passItemHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addCheck.isChecked())
                    {
                        passItemHeader.setAlpha(0.3f);
                    }
                    else
                        passItemHeader.setAlpha(1);
                    addCheck.setChecked(!addCheck.isChecked());
                    removeShowViews(addCheck.isChecked(),passItemBody);
                }
            });

            removeShowViews(false,passItemBody);
            passItemHeader.setAlpha(0.3f);

            final EditText amount = convertView.findViewById(R.id.amount);
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


            Spinner catDrop =  convertView.findViewById(R.id.catDrop);
            Spinner accountsDrop = convertView.findViewById(R.id.accountsDrop);

            accountsDrop.setAdapter(accAdapter);
            accountsDrop.setSelection(0);

            catDrop.setAdapter(catAdapter);
            catDrop.setSelection(0);

            return convertView;
        }
    }


}
