package moi.moneytracker.activities;


import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;

import moi.moneytracker.AndroidDatabaseManager;
import moi.moneytracker.fragments.AuthenticationFragment;
import moi.moneytracker.DatabaseHandler;
import moi.moneytracker.MTApp;
import moi.moneytracker.R;
import moi.moneytracker.TestActivity;
import moi.moneytracker.models.Transaction;
import moi.moneytracker.fragments.AlertFragment;
import moi.moneytracker.models.Account;

public class MainActivity extends AppCompatActivity implements AlertFragment.AlertDialogListener{

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    TextView textView;
    ExpandableListView monthTransactions;
    ArrayList<ArrayList<Transaction>> transactions;
    ArrayList<Account> accounts;
    int month;
    int year;
    int[] minMaxDates;

    float touchStart, touchEnd;
    final int MIN_DISTANCE = 500;

    // Navigation
    NavigationView navigationPanel;
//    ListView navigationList;

    DatabaseHandler db;

    private boolean authenticated;


//    private final String[] NAVIGATIONITEMS = getResources().getStringArray(R.array.navigationItems);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = MTApp.getDatabase();

        authenticated = false;

        if ( db.passIsEnabled())
        {
            DialogFragment df = new AuthenticationFragment();
            df.show(getSupportFragmentManager(),"authenticate");
        }
        else
            logIn();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (authenticated)
        {
            minMaxDates = db.getMinMaxDates();
            refreshMatrix();
            ((MyExpandableListAdapter) monthTransactions.getExpandableListAdapter()).notifyDataSetChanged();
            navigationPanel.getMenu().getItem(2).setEnabled(db.locationJobIsEnabled());
        }
    }

    private void refreshMatrix()
    {
        accounts = (ArrayList) db.getAccounts();
        transactions = new ArrayList<ArrayList<Transaction>>();
        if  (accounts.size() > 0 )
        {
            for (int i = 0; i < accounts.size(); i++)
            {
                transactions.add((ArrayList) db.getMonthTransactions(accounts.get(i).getAccountId(),year,month,false));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mm_tb_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(this, AddTransactionActivity.class);

        switch (item.getItemId()) {
            case R.id.add_income:
            {
                intent.putExtra("type","1");
                startActivity(intent);
                return true;
            }

            case R.id.add_expense:
            {
                intent.putExtra("type","0");
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object toDelete) {
        if (toDelete instanceof Transaction)
        {
            deleteTransaction((Transaction)toDelete);
        }
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog, Object toDelete) {
    }


    private void deleteTransaction(Transaction toDelete)
    {
        db.deleteTransaction(toDelete);
        minMaxDates = db.getMinMaxDates();
        refreshMatrix();
        ((MyExpandableListAdapter)monthTransactions.getExpandableListAdapter()).notifyDataSetChanged();
    }

    public void logIn()
    {
        authenticated = true;
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        minMaxDates = db.getMinMaxDates();

        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);

        toolbar = (Toolbar) findViewById(R.id.mainMenuTB);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mainMenu);

        textView = (TextView) findViewById(R.id.textView);

        refreshMatrix();

        monthTransactions = (ExpandableListView) findViewById(R.id.mMListView);
        monthTransactions.setAdapter(new MyExpandableListAdapter());

        ////////////
        navigationPanel = (NavigationView) findViewById(R.id.navigationPanel);

        navigationPanel.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                drawerLayout.closeDrawer( GravityCompat.START, true);
                String str = item.getTitle().toString();
                Log.d("xyz:","str is " + str);
                if (str.equals(getString(R.string.accounts)))
                {
                    Log.d("xyz:","str is here" );
                    Intent intent = new Intent(getBaseContext(), AccountsActivity.class);
                    startActivity(intent);
                }
                else if (str.equals(getString(R.string.recursiveTransactions)))
                {
                    Intent intent = new Intent(getBaseContext(), RecTransactionsActivity.class);
                    startActivity(intent);
                }
                else if (str.equals(getString(R.string.testActivity)))
                {
                    Intent intent = new Intent(getBaseContext(), TestActivity.class);
                    startActivity(intent);
                }
                else if (str.equals(getString(R.string.viewDatabase)))
                {
                    Intent intent = new Intent(getBaseContext(), AndroidDatabaseManager.class);
                    startActivity(intent);
                }
                else if (str.equals(getString(R.string.statistics)))
                {
                    Intent intent = new Intent(getBaseContext(), StatisticsActivity.class);
                    startActivity(intent);
                }
                else if (str.equals(getString(R.string.locations)))
                {
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    startActivity(intent);
                }
                else if (str.equals(getString(R.string.passBys)))
                {
                    Intent intent = new Intent(getBaseContext(), PassBysActivity.class);
                    startActivity(intent);
                }
                else if(str.equals(getString(R.string.settings)))
                {
                    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

//        String[] navigationArray = new String[]{"Accounts","Statistics","Locations","Pass Bys","Settings"};
////        ,"View Database"
//        navigationList = (ListView) findViewById(R.id.navigationList);
//        navigationList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, navigationArray));
//
//        navigationList.setOnItemClickListener(new DrawerItemClickListener());
//        ((DrawerI)navigationList.getItemAtPosition(2)).setEnabled(false);


    }

    public void setAuthenticated(boolean auth)
    {
        authenticated = auth;
    }


    public void dismissed()
    {
        if (authenticated)
            logIn();
        else
            finish();
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {


        @Override
        public int getGroupCount() {
            return accounts.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            Log.d("xyz:", "account " + groupPosition + " has " + transactions.get(groupPosition).size() + " transactions");
            return transactions.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return accounts.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return transactions.get(groupPosition).get(childPosititon);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            Account account = accounts.get(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.ex_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(account.getAccountName() + ": " + account.getBalance() + " " + account.getCurrency());

            return convertView;
        }

        @Override
        public View getChildView(final int groupPos, final int childPos, boolean isLastChild, View convertView, ViewGroup viewGroup)
        {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.ex_list_item, null);
            }

            final Transaction transaction = (Transaction) getChild(groupPos,childPos);

            TextView transDetails = (TextView) convertView.findViewById(R.id.transDetails);
            ImageButton deleteTransBtn = (ImageButton) convertView.findViewById(R.id.deleteTransBtn);

            transDetails.setText(transaction.getAmount() + " : " + transaction.getNotes());
            deleteTransBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment df = new AlertFragment();
                    ((AlertFragment)df).setMessage(getString(R.string.delTransAlertMsg));
                    ((AlertFragment)df).setToDeleteObject(transaction);
                    df.show(getSupportFragmentManager(),"deleteTransaction");
                }
            });
            return convertView;
        }


    }

/*
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            drawerLayout.closeDrawer( GravityCompat.START, true);
            switch (position)
            {
                case 0:
                {
                    Intent intent = new Intent(getBaseContext(),AccountsActivity.class);
                    startActivity(intent);
                    break;
                }
//                case 1:
//                {
//                    Intent intent = new Intent(getBaseContext(),AndroidDatabaseManager.class);
//                    startActivity(intent);
//                    break;
//                }
                case 1:
                {
                    Intent intent = new Intent(getBaseContext(),StatisticsActivity.class);
                    startActivity(intent);
                    break;
                }
                case 2:
                {
                    Intent intent = new Intent(getBaseContext(),MapsActivity.class);
                    startActivity(intent);
                    break;
                }
                case 3:
                {
                    Intent intent = new Intent(getBaseContext(),PassBysActivity.class);
                    startActivity(intent);
                    break;
                }
                case 4:
                {
                    Intent intent = new Intent(getBaseContext(),SettingsActivity.class);
                    startActivity(intent);
                    break;
                }
                default:
                    break;
            }

        }
    }
*/

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START,true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                touchStart = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                touchEnd = event.getX();
                float deltaX = touchEnd - touchStart;
                if ( Math.abs(deltaX) >= MIN_DISTANCE && deltaX > 0 )
                {
                    prevMonth();
                }
                else if ( Math.abs(deltaX) >= MIN_DISTANCE && deltaX < 0 )
                {
                    nextMonth();
                }
                break;
            }
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }



    private void prevMonth()
    {
        int oldYear = year;
        int oldMonth = month;

        if ( month == 1 )
        {
            month = 12;
            year--;
        }
        else
            month--;

        int result = compareMyDates(minMaxDates[0],minMaxDates[1],year,month);
        if (result == -1)
        {
            year = oldYear;
            month = oldMonth;
        }

        refreshMatrix();
        ((MyExpandableListAdapter)monthTransactions.getExpandableListAdapter()).notifyDataSetChanged();
        updateMonthLabel();
    }

    private void nextMonth()
    {
        int oldYear = year;
        int oldMonth = month;

        if ( month == 12 )
        {
            month = 1;
            year++;
        }
        else
            month++;

        int result = compareMyDates(minMaxDates[2],minMaxDates[3],year,month);
        if (result == 1)
        {
            year = oldYear;
            month = oldMonth;
        }

        refreshMatrix();
        ((MyExpandableListAdapter)monthTransactions.getExpandableListAdapter()).notifyDataSetChanged();
        updateMonthLabel();
    }

    private void updateMonthLabel(){
        if ( month == Calendar.getInstance().get(Calendar.MONTH) + 1 && year == Calendar.getInstance().get(Calendar.YEAR))
            textView.setText(R.string.expensesOfThisMonth);
        else
        {
            String monthStr = month + "";
            while(monthStr.length() != 2)
                monthStr = "0" + monthStr;
            textView.setText(getString(R.string.expensesOf) + " " + monthStr + " / " + year);
        }
    }

    private int compareMyDates( int year, int month, int year2, int month2 )
    {
        if (year2 > year)
            return 1;
        else if ( year > year2 )
            return -1;
        else
        {
            if (month2 > month)
                return 1;
            else if( month > month2)
                return -1;
            else
                return 0;
        }
    }

    public boolean checkDbPass( String pass)
    {
        return db.checkPassword(pass);
    }


}
