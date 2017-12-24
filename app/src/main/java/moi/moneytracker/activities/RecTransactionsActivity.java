package moi.moneytracker.activities;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.evernote.android.job.JobManager;

import java.util.List;

import moi.moneytracker.BkgRecTransactionsJob;
import moi.moneytracker.DatabaseHandler;
import moi.moneytracker.fragments.EditRecTransactionFragment;
import moi.moneytracker.MTApp;
import moi.moneytracker.R;
import moi.moneytracker.fragments.AlertFragment;
import moi.moneytracker.models.Account;
import moi.moneytracker.models.RecTransaction;
import moi.moneytracker.models.Transaction;

public class RecTransactionsActivity extends AppCompatActivity implements AlertFragment.AlertDialogListener{


    Toolbar toolbar;
    ListView recTransactiosListView;
    List<RecTransaction> recTransactions;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_transactions);

        toolbar = (Toolbar) findViewById(R.id.recTransactionsTB);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.recursiveTransactions);

        db = MTApp.getDatabase();

        recTransactions = db.getRecTransactions();
        recTransactiosListView = (ListView) findViewById(R.id.recTransactionsListView);
        recTransactiosListView.setAdapter(new RecTransactionsActivity.RecTransactionsListAdapter());
    }

    /////////////
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object toDelete) {
        if (toDelete instanceof RecTransaction)
        {
            deleteRecTransaction((RecTransaction)toDelete);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, Object toDelete) {

    }


    private class RecTransactionsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return recTransactions.size();
        }

        @Override
        public String getItem(int position) {
            return "";
        }

        @Override
        public long getItemId(int position) {
            return recTransactions.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.rectransactions_list_item, container, false);
            }
            final RecTransaction recTransaction = recTransactions.get(position);
            final Transaction transaction = db.getTransaction(recTransaction.getRefTransactionId());
            final Account account = db.getAccount(transaction.getRefAccountId());

            final String fromAccountStr = getString(R.string.from) + " " + account.getAccountName();
            ((TextView) convertView.findViewById(R.id.amountFromAcnt)).setText(recTransaction.getAmount() + " " + fromAccountStr);

            final String catNotesStr = transaction.getCategory()
                    + ( transaction.getNotes().equals("") ? "" : (": " + transaction.getNotes()));
            ((TextView) convertView.findViewById(R.id.catNotes)).setText(catNotesStr);

            ((ImageButton) convertView.findViewById(R.id.deleteRecTransactionBtn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment df = new AlertFragment();
                    ((AlertFragment)df).setMessage( getString(R.string.delRecTransactionAlertMsg) );
                    ((AlertFragment)df).setToDeleteObject(recTransaction);
                    df.show(getSupportFragmentManager(),"deleteRecTransaction");
                }
            });

            ((ImageButton) convertView.findViewById(R.id.editRecTransactionBtn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment df = new EditRecTransactionFragment();
                    ((EditRecTransactionFragment)df).setToEditObject(recTransaction);
                    ((EditRecTransactionFragment)df).setOriginalDate(transaction.getDate());
                    ((EditRecTransactionFragment)df).setFromAcnt(fromAccountStr);
                    ((EditRecTransactionFragment)df).setCatNotes(catNotesStr);
                    df.show(getSupportFragmentManager(),"editRecTransaction");
                }
            });

            return convertView;
        }
    }

    public void deleteRecTransaction(RecTransaction recTransaction)
    {
        db.deleteRecTransaction(recTransaction);
        recTransactions = db.getRecTransactions();
        ((RecTransactionsActivity.RecTransactionsListAdapter)recTransactiosListView.getAdapter()).notifyDataSetChanged();

        if (db.getRecTransactionsCount() == 0)
        {
            JobManager manager = ((MTApp) getApplication()).getJobManager();
            manager.cancelAllForTag(BkgRecTransactionsJob.TAG);
            Log.d("xyz:","recTransaction job canceled");
        }
    }

//    public void addAccount( String name, String cur, double bal)
//    {
//        Account account = new Account(name,cur,bal);
//        db.addAccount(account);
//        accounts = db.getAccounts();
//        accountsListView.invalidateViews();
//    }

    public void editRecTransaction( RecTransaction recTransaction )
    {
        db.editRecTransaction(recTransaction);
        recTransactions = db.getRecTransactions();
        ((RecTransactionsActivity.RecTransactionsListAdapter)recTransactiosListView.getAdapter()).notifyDataSetChanged();
    }

}
