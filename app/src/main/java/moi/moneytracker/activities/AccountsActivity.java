package moi.moneytracker.activities;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

import moi.moneytracker.fragments.AddAccountFragment;
import moi.moneytracker.fragments.AlertFragment;
import moi.moneytracker.DatabaseHandler;
import moi.moneytracker.MTApp;
import moi.moneytracker.R;
import moi.moneytracker.models.Account;

public class AccountsActivity extends AppCompatActivity implements AlertFragment.AlertDialogListener
{

    Toolbar toolbar;
    ListView accountsListView;
    List<Account> accounts;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        db = MTApp.getDatabase();

        toolbar = (Toolbar) findViewById(R.id.accountsTB);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.accounts);



        accounts = db.getAccounts();
        accountsListView = (ListView) findViewById(R.id.accountsListView);
        accountsListView.setAdapter(new AccountsListAdapter());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.accounts_tb_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
            {
                DialogFragment df = new AddAccountFragment();
                df.show(getSupportFragmentManager(),"addAccount");
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /////////////
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object toDelete) {
        if (toDelete instanceof Account)
        {
            deleteAccount((Account)toDelete);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, Object toDelete) {

    }


    private class AccountsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return accounts.size();
        }

        @Override
        public String getItem(int position) {
            return "";
        }

        @Override
        public long getItemId(int position) {
            return accounts.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.accounts_list_item, container, false);
            }
            final Account acc = accounts.get(position);

            ((TextView) convertView.findViewById(R.id.accountNameView)).setText(acc.getAccountName());

            ((TextView) convertView.findViewById(R.id.balCurView)).setText(acc.getBalance() + " " + acc.getCurrency() );

            ((ImageButton) convertView.findViewById(R.id.deleteAccountBtn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment df = new AlertFragment();
                    ((AlertFragment)df).setMessage( getString(R.string.delAcntAlertMsg) );
                    ((AlertFragment)df).setToDeleteObject(acc);
                    df.show(getSupportFragmentManager(),"deleteAccount");
                }
            });
            return convertView;
        }
    }

    public void deleteAccount(Account account)
    {
        db.deleteAccount(account);
        accounts = db.getAccounts();
        ((AccountsListAdapter)accountsListView.getAdapter()).notifyDataSetChanged();
    }

    public void addAccount( String name, String cur, double bal)
    {
        Account account = new Account(name,cur,bal);
        db.addAccount(account);
        accounts = db.getAccounts();
        accountsListView.invalidateViews();
    }

}


