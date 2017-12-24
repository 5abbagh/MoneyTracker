package moi.moneytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import moi.moneytracker.models.Account;
import moi.moneytracker.models.MapLocation;
import moi.moneytracker.models.PassBy;
import moi.moneytracker.models.RecTransaction;
import moi.moneytracker.models.Transaction;

/**
 * Created by Ali on 01-Nov-17.
 */

public class DatabaseHandler extends SQLiteOpenHelper
{

    private static DatabaseHandler instance;

    public static DatabaseHandler getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseHandler(context.getApplicationContext());
        }
        return instance;
    }

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MoneyTrackerDB";

    // Tables Names
    private static final String TABLE_TRANSACTION = "TransactionTB";
    private static final String TABLE_ACCOUNT = "Account";
    private static final String TABLE_MAPLOCATION = "MapLocation";
    private static final String TABLE_PASSBY = "PassBy";
    private static final String TABLE_SETTINGS = "Settings";
    private static final String TABLE_RECTRANSACTION = "RecTransaction";


    // Transaction Table Columns names
    private static final String COLUMN_ID = "TrId";
    private static final String COLUMN_REFACCOUNTID = "RefAccountId";
    private static final String COLUMN_TYPE = "TrType";
    private static final String COLUMN_DATE = "TrDate";
    private static final String COLUMN_AMOUNT = "TrAmount";
    private static final String COLUMN_NOTES = "TrNotes";
    private static final String COLUMN_CATEGORY = "TrCategory";
//    private static final String COLUMN_RECURSIVE = "TrRecursive";
//    private static final String COLUMN_FREQ = "TrFreq";
//    private static final String COLUMN_FREQUNIT = "TrFreqUnit";
//    private static final String COLUMN_END = "TrEnd";
//    private static final String COLUMN_ENDUNIT = "TrEndUnit";

    // Account Table Columns names
    private static final String COLUMN_ACCOUNTID = "AccountId";
    private static final String COLUMN_ACNAME = "AcName";
    private static final String COLUMN_ACCURRENCY = "AcCurrency";
    private static final String COLUMN_ACBALANCE = "AcBalance";

    // MapLocation Table Columns names
    private static final String COLUMN_LOCID = "LocId";
    private static final String COLUMN_LOCNAME = "LocName";
    private static final String COLUMN_LAT = "Lat";
    private static final String COLUMN_LNG = "Lng";

    // PassBy Table Columns names
    private static final String COLUMN_PASSID = "PassId";
    private static final String COLUMN_REFMAPLOCATION = "RefMapLocation";
    private static final String COLUMN_PASSDATE = "PassDate";

    // Settings Table Columns names
    private static final String COLUMN_PASSENABLED = "PassEnabled";
    private static final String COLUMN_PASSWORD = "Password";
    private static final String COLUMN_LOCATIONJOB = "LocationJob";


    // RecTransaction Table columns names
    private static final String COLUMN_RECID = "RecId";
    private static final String COLUMN_REFTRANSACTIONID = "RefTransactionId";
    private static final String COLUMN_RECAMOUNT = "RecAmount";

    private static final String COLUMN_EVERYNUMBER = "EveryNum";
    private static final String COLUMN_EVERYUNIT = "EveryUnit";
    private static final String COLUMN_FORNUMBER = "ForNum";
    private static final String COLUMN_FORUNIT = "ForUnit";
//    private static final String COLUMN_EVERYNDAYS = "EveryNDays";
//    private static final String COLUMN_ENDDATE = "EndDate";
    private static final String COLUMN_LASTEXDATE = "LastExDate";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        CreateAccountTable( db );
        CreateTransactionTable( db );
        CreateMapLocationTable( db );
        CreatePassByTable( db );
        CreateSettingsTable(db);
        CreateRecTransactionTable(db);
        fillTestData(db);
        Log.d("xyz:","Tables Created");
    }

    private void CreatePassByTable(SQLiteDatabase db)
    {
        String CREATE_PASSBY_TABLE = "CREATE TABLE " + TABLE_PASSBY + "("
                + COLUMN_PASSID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_REFMAPLOCATION + " INTEGER REFERENCES "
                + TABLE_MAPLOCATION + "(" + COLUMN_LOCID + "),"
                + COLUMN_PASSDATE + " TEXT" + ")";
        db.execSQL(CREATE_PASSBY_TABLE);
        Log.d("xyz:","PassBy Table Created");
    }

    private void CreateMapLocationTable(SQLiteDatabase db)
    {
        String CREATE_MAPLOCATION_TABLE = "CREATE TABLE " + TABLE_MAPLOCATION + "("
                + COLUMN_LOCID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_LOCNAME + " TEXT,"
                + COLUMN_LAT + " REAL," + COLUMN_LNG + " REAL" + ")";
        db.execSQL(CREATE_MAPLOCATION_TABLE);
        Log.d("xyz:","MapLocation Table Created");
    }

    private void CreateSettingsTable(SQLiteDatabase db)
    {
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "(" + COLUMN_PASSENABLED + " BOOLEAN,"
                + COLUMN_PASSWORD + " TEXT," + COLUMN_LOCATIONJOB + " BOOLEAN" + ")";
        db.execSQL(CREATE_SETTINGS_TABLE);

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSENABLED, false);
        values.put(COLUMN_PASSWORD, "");
        values.put(COLUMN_LOCATIONJOB, true);

        // Inserting Row
        db.insert(TABLE_SETTINGS, null, values);

        Log.d("xyz:","Settings Table Created");
    }

    private void CreateAccountTable( SQLiteDatabase db )
    {

        String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + "("
                + COLUMN_ACCOUNTID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ACNAME + " TEXT,"
                + COLUMN_ACCURRENCY + " TEXT," + COLUMN_ACBALANCE + " REAL" + ")";
        db.execSQL(CREATE_ACCOUNT_TABLE);
        Log.d("xyz:","Account Created");
    }

    private void CreateTransactionTable( SQLiteDatabase db )
    {

        String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_REFACCOUNTID + " INTEGER REFERENCES "
                + TABLE_ACCOUNT + "(" + COLUMN_ACCOUNTID + "),"
                + COLUMN_TYPE + " BOOLEAN," + COLUMN_DATE + " TEXT," + COLUMN_AMOUNT + " REAL,"
                + COLUMN_NOTES + " TEXT," + COLUMN_CATEGORY + " TEXT" +  ")";

//        COLUMN_RECURSIVE + " BOOLEAN," + COLUMN_FREQ + " INTEGER," + COLUMN_FREQUNIT + " TEXT," + COLUMN_END + " INTEGER," + COLUMN_ENDUNIT + " TEXT" +
        db.execSQL(CREATE_TRANSACTION_TABLE);
        Log.d("xyz:","TransactionTB Created");
    }

    private void CreateRecTransactionTable( SQLiteDatabase db )
    {

        String CREATE_RECTRANSACTION_TABLE = "CREATE TABLE " + TABLE_RECTRANSACTION + "("
                + COLUMN_RECID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_REFTRANSACTIONID + " INTEGER REFERENCES "
                + TABLE_TRANSACTION + "(" + COLUMN_ID + "),"
                + COLUMN_RECAMOUNT + " REAL," + COLUMN_EVERYNUMBER + " INTEGER," + COLUMN_EVERYUNIT + " TEXT,"
                + COLUMN_FORNUMBER + " INTEGER," + COLUMN_FORUNIT + " TEXT," + COLUMN_LASTEXDATE + " TEXT" + ")";
        db.execSQL(CREATE_RECTRANSACTION_TABLE);
        Log.d("xyz:","RecTransaction Table Created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECTRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSBY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAPLOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);

        // Create tables again
        onCreate(db);
    }


    // CRUD Methods

    public Account getAccount(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * from " + TABLE_ACCOUNT + " where " + COLUMN_ACCOUNTID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);

        if( cursor != null )
        {
            cursor.moveToFirst();
            Account account = new Account(cursor);
            cursor.close();
            return account;
        }
        return null;
    }

    // Adding new Account
    public void addAccount(Account account)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ACNAME, account.getAccountName());
        values.put(COLUMN_ACCURRENCY, account.getCurrency());
        values.put(COLUMN_ACBALANCE, account.getBalance());

        // Inserting Row
        db.insert(TABLE_ACCOUNT, null, values);

    }

    // Deleting single Account
    public void deleteAccount(Account account) {

        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_ACCOUNT, COLUMN_ACCOUNTID + " = ?",
//                new String[] { String.valueOf(account.getAccountId()) });
        String deleteTransactions = "Delete from " + TABLE_TRANSACTION
                                    + " where " + COLUMN_REFACCOUNTID + " = " + account.getAccountId();
        String query = "Delete from " + TABLE_ACCOUNT + " where " + COLUMN_ACCOUNTID + " = " + account.getAccountId();
        db.execSQL(deleteTransactions);
        db.execSQL(query);
    }


    // Getting All Transactions
    public List<Account> getAccounts()
    {
        List<Account> accountList = new ArrayList<Account>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ACCOUNT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Account account = new Account( cursor );
                accountList.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accountList;
    }

    // Getting all accounts Count
    public int getAccountsCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT count(" + COLUMN_ACCOUNTID + ") FROM " + TABLE_ACCOUNT;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = Integer.parseInt(cursor.getString(0));
        cursor.close();
        return count;
//        String countQuery = "SELECT * FROM " + TABLE_TRANSACTION;
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
    }


    public List<Transaction> getMonthTransactions(int accountId, int year, int month, boolean onlyExpenses )
    {

        SQLiteDatabase db = this.getReadableDatabase();

        String yearStr = year + "";
        while(yearStr.length() != 4)
            yearStr = "0" + yearStr;

        String monthStr = month + "";
        while(monthStr.length() != 2)
            monthStr = "0" + monthStr;

        String queryDate = yearStr + "-" + monthStr;

        String query;
        if (onlyExpenses)
        {
            query = "Select * from " + TABLE_TRANSACTION + " join " + TABLE_ACCOUNT
                    + " on " + COLUMN_ACCOUNTID + " = " + COLUMN_REFACCOUNTID
                    + " where " + COLUMN_ACCOUNTID + " = " + accountId
                    + " AND " + COLUMN_DATE + " like '" + queryDate + "%'"
                    + " AND " + COLUMN_TYPE + " = 0";
        }
        else
        {
            query = "Select * from " + TABLE_TRANSACTION + " join " + TABLE_ACCOUNT
                    + " on " + COLUMN_ACCOUNTID + " = " + COLUMN_REFACCOUNTID
                    + " where " + COLUMN_ACCOUNTID + " = " + accountId
                    + " AND " + COLUMN_DATE + " like '" + queryDate + "%'";
        }

        Cursor cursor = db.rawQuery(query, null);

        List<Transaction> transactionList = new ArrayList<Transaction>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Transaction transaction = new Transaction( cursor );
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return transaction list
        return transactionList;
    }

    public int getMonthTransactionsCount( int accountId, int year, int month)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String queryDate = yearToString(year) + "-" + monthToString(month);

        String query = "Select count(" + COLUMN_ID + ") from " + TABLE_TRANSACTION + " join " + TABLE_ACCOUNT
                + " on " + COLUMN_ACCOUNTID + " = " + COLUMN_REFACCOUNTID
                + " where " + COLUMN_ACCOUNTID + " = " + accountId
                + " AND " + COLUMN_DATE + " like '" + queryDate + "%'";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        int result = Integer.valueOf(cursor.getString(0));
        cursor.close();
        return result;
    }

    public double[] getMonthTotals( int accountId, int year, int month)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String yearStr = year + "";
        while(yearStr.length() != 4)
            yearStr = "0" + yearStr;

        String monthStr = month + "";
        while(monthStr.length() != 2)
            monthStr = "0" + monthStr;

        String queryDate = yearStr + "-" + monthStr;

        String query = "Select  sum(" + COLUMN_AMOUNT + "), " + COLUMN_TYPE + " from " + TABLE_TRANSACTION + " join " + TABLE_ACCOUNT
                + " on " + COLUMN_ACCOUNTID + " = " + COLUMN_REFACCOUNTID
                + " where " + COLUMN_ACCOUNTID + " = " + accountId
                + " AND " + COLUMN_DATE + " like '" + queryDate + "%'"
                + " group by " + COLUMN_TYPE + " order by " + COLUMN_TYPE + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        double[] result = new double[2];

        // expense
        if (cursor.moveToFirst())
            result[0] = Double.valueOf(cursor.getString(0));

        // income
        if (cursor.moveToNext())
            result[1] = Double.valueOf(cursor.getString(0));

        cursor.close();
        return result;
    }

    public ArrayList<String> getMonthCategoriesExpenses( int accountId, int year, int month)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String yearStr = year + "";
        while(yearStr.length() != 4)
            yearStr = "0" + yearStr;

        String monthStr = month + "";
        while(monthStr.length() != 2)
            monthStr = "0" + monthStr;

        String queryDate = yearStr + "-" + monthStr;

        String query = "Select  sum(" + COLUMN_AMOUNT + "), " + COLUMN_CATEGORY + " from " + TABLE_TRANSACTION + " join " + TABLE_ACCOUNT
                + " on " + COLUMN_ACCOUNTID + " = " + COLUMN_REFACCOUNTID
                + " where " + COLUMN_ACCOUNTID + " = " + accountId
                + " AND " + COLUMN_TYPE + " = 0"
                + " AND " + COLUMN_DATE + " like '" + queryDate + "%'"
                + " group by " + COLUMN_CATEGORY + " order by sum(" + COLUMN_AMOUNT + ") DESC";



        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> result = new ArrayList<String>();

        // expense

        if (cursor.moveToFirst())
        {
            do
            {
                result.add(cursor.getString(0));
                result.add(cursor.getString(1));
            } while( cursor.moveToNext());
        }
        cursor.close();
        return result;
    }


    public ArrayList<String> getYearCategoriesExpenses( int accountId, int year )
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String queryDate = yearToString(year);

        String query = "Select  sum(" + COLUMN_AMOUNT + "), " + COLUMN_CATEGORY + " from " + TABLE_TRANSACTION + " join " + TABLE_ACCOUNT
                + " on " + COLUMN_ACCOUNTID + " = " + COLUMN_REFACCOUNTID
                + " where " + COLUMN_ACCOUNTID + " = " + accountId
                + " AND " + COLUMN_TYPE + " = 0"
                + " AND " + COLUMN_DATE + " like '" + queryDate + "%'"
                + " group by " + COLUMN_CATEGORY + " order by sum(" + COLUMN_AMOUNT + ") DESC";



        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> result = new ArrayList<String>();

        // expense

        if (cursor.moveToFirst())
        {
            do
            {
                result.add(cursor.getString(0));
                result.add(cursor.getString(1));
            } while( cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<String> getYearMonthlyExpenses(int accountId, int year)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String queryDate = yearToString(year);

        String query = "Select  sum(" + COLUMN_AMOUNT + "), substr(" + COLUMN_DATE + ",6,2) as month from " + TABLE_TRANSACTION
                + " join " + TABLE_ACCOUNT
                + " on " + COLUMN_ACCOUNTID + " = " + COLUMN_REFACCOUNTID
                + " where " + COLUMN_ACCOUNTID + " = " + accountId
                + " AND " + COLUMN_TYPE + " = 0"
                + " AND " + COLUMN_DATE + " like '" + queryDate + "%'"
                + " group by month order by month ASC";



        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> result = new ArrayList<String>();

        // expense

        if (cursor.moveToFirst())
        {
            do
            {
                result.add(cursor.getString(0));
            } while( cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public String[] getAccountYears(Account account)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select substr(" + COLUMN_DATE + ",1,4) as year from " + TABLE_TRANSACTION
                        + " where " + COLUMN_REFACCOUNTID + " = " + account.getAccountId()
                        + " group by year order by year DESC";
        Cursor cursor = db.rawQuery(query,null);

        String[] years = new String[cursor.getCount()];

        if (cursor.moveToFirst())
        {
            int i = 0;
            do {
                years[i] = cursor.getString(0);
                i++;

            } while(cursor.moveToNext());
        }
        cursor.close();
        return years;
    }

    /////////////////////////////////////////////////////////////////////

    // Getting single transaction
    public Transaction getTransaction(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_TRANSACTION, new String[] { COLUMN_ID,
//                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);

        String query = "Select * from " + TABLE_TRANSACTION + " where " + COLUMN_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
            Transaction transaction = new Transaction( cursor );
            cursor.close();
            return transaction;
        }
        return null;
    }

    public Transaction getLastAddedTransaction()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * from " + TABLE_TRANSACTION + " order by " + COLUMN_ID + " desc limit 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
            Transaction transaction = new Transaction( cursor );
            cursor.close();
            return transaction;
        }
        return null;
    }

    // Adding new Transaction
    public void addTransaction(Transaction transaction)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REFACCOUNTID, transaction.getRefAccountId());
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_DATE, transaction.getDate());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_NOTES, transaction.getNotes());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
//        values.put(COLUMN_RECURSIVE, transaction.isRecursive());
//        if (transaction.isRecursive())
//        {
//            values.put(COLUMN_FREQ, transaction.getFreq());
//            values.put(COLUMN_FREQUNIT, transaction.getFreqUnit());
//            values.put(COLUMN_END, transaction.getEnd());
//            values.put(COLUMN_ENDUNIT, transaction.getEndUnit());
//        }

        // Inserting Row
        db.insert(TABLE_TRANSACTION, null, values);


        Cursor cursor = db.rawQuery("Select " + COLUMN_ACBALANCE + " from " + TABLE_ACCOUNT
                                    + " where " + COLUMN_ACCOUNTID + " = " + transaction.getRefAccountId(),null);
        cursor.moveToFirst();
        String oldBalanceStr = cursor.getString(0);
        double oldBalance = Double.valueOf(oldBalanceStr);
        double newBalance = transaction.getType() ? (oldBalance + transaction.getAmount()) : (oldBalance - transaction.getAmount());
        ContentValues balance = new ContentValues();
        balance.put(COLUMN_ACBALANCE, newBalance);
        // updating account

        db.update(TABLE_ACCOUNT, balance, COLUMN_ACCOUNTID + " = ?",
                new String[] { String.valueOf(transaction.getRefAccountId()) });

        cursor.close();
    }

    // Editing single Transaction
    public int editTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REFACCOUNTID, transaction.getRefAccountId());
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_DATE, transaction.getDate());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_NOTES, transaction.getNotes());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
//        values.put(COLUMN_RECURSIVE, transaction.isRecursive());
//        if (transaction.isRecursive())
//        {
//            values.put(COLUMN_FREQ, transaction.getFreq());
//            values.put(COLUMN_FREQUNIT, transaction.getFreqUnit());
//            values.put(COLUMN_END, transaction.getEnd());
//            values.put(COLUMN_ENDUNIT, transaction.getEndUnit());
//        }

        // updating row
        return db.update(TABLE_TRANSACTION, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(transaction.getID()) });
    }

    // Deleting single transaction
    public void deleteTransaction(Transaction transaction) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTION, COLUMN_ID + " = ?",
                new String[] { String.valueOf(transaction.getID()) });

        Cursor cursor = db.rawQuery("Select " + COLUMN_ACBALANCE + " from " + TABLE_ACCOUNT
                + " where " + COLUMN_ACCOUNTID + " = " + transaction.getRefAccountId(),null);
        cursor.moveToFirst();
        String oldBalanceStr = cursor.getString(0);
        double oldBalance = Double.valueOf(oldBalanceStr);
        double newBalance = transaction.getType() ? (oldBalance - transaction.getAmount()) : (oldBalance + transaction.getAmount());
        ContentValues balance = new ContentValues();
        balance.put(COLUMN_ACBALANCE, newBalance);
        // updating account
        db.update(TABLE_ACCOUNT, balance, COLUMN_ACCOUNTID + " = ?",
                new String[] { String.valueOf(transaction.getRefAccountId()) });

        cursor.close();
    }


    // Getting All Transactions
    public List<Transaction> getAllTransactions()
    {
        List<Transaction> transactionList = new ArrayList<Transaction>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Transaction transaction = new Transaction( cursor );
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return transaction list
        return transactionList;
    }

    // Getting all transactions Count
    public int getTransactionsCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT count(" + COLUMN_ID + ") FROM " + TABLE_TRANSACTION;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = Integer.parseInt(cursor.getString(0));
        cursor.close();
        return count;
//        String countQuery = "SELECT * FROM " + TABLE_TRANSACTION;
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
    }


    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }


    private void fillTestData(SQLiteDatabase db)
    {
        createInitialAccounts(db);
        int yearsFilled = 0;
        int month = 1;
        int monthTrans = 0;

        while (yearsFilled != 2)
        {
            while ( monthTrans < 20 )
            {
                Transaction transaction = new Transaction();
                transaction.setRefAccountId(1);

//                int randomType = (int) (Math.random()+1);
                transaction.setType( false);

                String[] categoriesArray = new String[]{"Bills","Clothes","Entertainment","Food","Transportation","Other"};
//                if (randomType == 1)
//                    transaction.setCategory("");
//                else
//                {
                    int randomCat = (int) (Math.random()*6);
                    transaction.setCategory(categoriesArray[randomCat]);
//                }

//                transaction.setRecursive(false);

                int day = (int) (Math.random()*29);
                transaction.setDate( (2017 - yearsFilled) + "-" + monthToString(month) + "-" + day);

                double amount = (Math.random()*501);
                transaction.setAmount(amount);

                transaction.setNotes("");

                addTrans(db,transaction);
                monthTrans++;
            }
            monthTrans = 0;
            if (month == 12)
            {
                month = 1;
                yearsFilled++;
            }
            else
                month++;
        }
    }

    private void createInitialAccounts( SQLiteDatabase db)
    {
        String[] names = new String[]{"Cash","Bank","Credit Card"};
        String[] curs = new String[]{"USD","EURO","DKK"};
        for (int i = 0; i < 3; i++)
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ACNAME, names[i]);
            values.put(COLUMN_ACCURRENCY, curs[i]);
            values.put(COLUMN_ACBALANCE, 240500);

            // Inserting Row
            db.insert(TABLE_ACCOUNT, null, values);
        }
    }

    public static String monthToString(int month)
    {
        String monthStr = month + "";
        while(monthStr.length() != 2)
            monthStr = "0" + monthStr;
        return monthStr;
    }

    public static String yearToString(int year)
    {
        String yearStr = year + "";
        while(yearStr.length() != 4)
            yearStr = "0" + yearStr;
        return yearStr;
    }


    private void addTrans( SQLiteDatabase db, Transaction transaction)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_REFACCOUNTID, transaction.getRefAccountId());
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_DATE, transaction.getDate());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_NOTES, transaction.getNotes());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
//        values.put(COLUMN_RECURSIVE, transaction.isRecursive());
//        if (transaction.isRecursive())
//        {
//            values.put(COLUMN_FREQ, transaction.getFreq());
//            values.put(COLUMN_FREQUNIT, transaction.getFreqUnit());
//            values.put(COLUMN_END, transaction.getEnd());
//            values.put(COLUMN_ENDUNIT, transaction.getEndUnit());
//        }

        // Inserting Row
        db.insert(TABLE_TRANSACTION, null, values);


        Cursor cursor = db.rawQuery("Select " + COLUMN_ACBALANCE + " from " + TABLE_ACCOUNT
                + " where " + COLUMN_ACCOUNTID + " = " + transaction.getRefAccountId(),null);
        cursor.moveToFirst();
        String oldBalanceStr = cursor.getString(0);
        double oldBalance = Double.valueOf(oldBalanceStr);
        double newBalance = transaction.getType() ? (oldBalance + transaction.getAmount()) : (oldBalance - transaction.getAmount());
        ContentValues balance = new ContentValues();
        balance.put(COLUMN_ACBALANCE, newBalance);
        // updating account
        db.update(TABLE_ACCOUNT, balance, COLUMN_ACCOUNTID + " = ?",
                new String[] { String.valueOf(transaction.getRefAccountId()) });


        cursor.close();
    }



    /////////////////////////////////////////////
    // Getting All MapLocations
    public List<MapLocation> getMapLocations()
    {
        List<MapLocation> mapLocations = new ArrayList<MapLocation>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_MAPLOCATION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                MapLocation location = new MapLocation( cursor );
                mapLocations.add(location);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapLocations;
    }

    // Getting all MapLocations Count
    public int getMapLocationsCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT count(" + COLUMN_LOCID + ") FROM " + TABLE_MAPLOCATION;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = Integer.parseInt(cursor.getString(0));
        cursor.close();
        return count;
    }

    public MapLocation getMapLocation(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * from " + TABLE_MAPLOCATION + " where " + COLUMN_LOCID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);

        if( cursor != null )
        {
            cursor.moveToFirst();
            MapLocation location = new MapLocation(cursor);
            cursor.close();
            return location;
        }
        return null;
    }

    // Adding new Account
    public void addMapLocation(MapLocation location)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCNAME, location.getName());
        values.put(COLUMN_LAT, location.getLat());
        values.put(COLUMN_LNG, location.getLng());

        // Inserting Row
        db.insert(TABLE_MAPLOCATION, null, values);
    }

    // Deleting single Account
    public void deleteMapLocation(MapLocation location) {

        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_ACCOUNT, COLUMN_ACCOUNTID + " = ?",
//                new String[] { String.valueOf(account.getAccountId()) });
        String query = "Delete from " + TABLE_MAPLOCATION + " where " + COLUMN_LOCID + " = " + location.getLocationId();
        db.execSQL(query);
    }


    //////////////////////////////////////////////


    // Getting All PassBys
    public List<PassBy> getPassBys()
    {
        List<PassBy> passes = new ArrayList<PassBy>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PASSBY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                PassBy pass = new PassBy( cursor );
                passes.add(pass);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return passes;
    }

    // Getting all PassBys Count
    public int getPassBysCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT count(" + COLUMN_PASSID + ") FROM " + TABLE_PASSBY;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = Integer.parseInt(cursor.getString(0));
        cursor.close();
        return count;
    }

    public PassBy getPassBy(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * from " + TABLE_PASSBY + " where " + COLUMN_PASSID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);

        if( cursor != null )
        {
            cursor.moveToFirst();
            PassBy pass = new PassBy(cursor);
            cursor.close();
            return pass;
        }
        return null;
    }

    // Adding new PassBy
    public void addPassBy(PassBy pass)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REFMAPLOCATION, pass.getRefMapLocation());
        values.put(COLUMN_PASSDATE, pass.getPassDate());

        // Inserting Row
        db.insert(TABLE_PASSBY, null, values);
    }

    // Deleting single PassBy
    public void deletePassBy(PassBy pass) {

        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_ACCOUNT, COLUMN_ACCOUNTID + " = ?",
//                new String[] { String.valueOf(account.getAccountId()) });
        String query = "Delete from " + TABLE_PASSBY + " where " + COLUMN_PASSID + " = " + pass.getPassId();
        db.execSQL(query);
    }

    // Deleting All PassBys
    public void deleteAllPassBys()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSBY);
        CreatePassByTable(db);
    }

    // get latest PassBy to a MapLocation
    public PassBy getLatestPassByTo(int mapLocationId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_PASSBY + " where " + COLUMN_REFMAPLOCATION + " = " + mapLocationId
                + " order by " + COLUMN_PASSID + " DESC limit 1";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            PassBy pass = new PassBy(cursor);
            cursor.close();
            return pass;
        }

        cursor.close();
        return null;
    }

    public int[] getMinMaxDates()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        int[] minMax = new int[4];

        String min = "Select " + COLUMN_DATE + " from " + TABLE_TRANSACTION + " order by " + COLUMN_DATE + " ASC limit 1";
        Cursor minCur = db.rawQuery(min,null);
        if (minCur.moveToFirst())
        {
            String minDate = minCur.getString(0);
            String minYear = minDate.substring(0,4);
            String minMonth = minDate.substring(5,7);

            minMax[0] = Integer.valueOf(minYear);
            minMax[1] = Integer.valueOf(minMonth);
        }
        minCur.close();

        String max = "Select " + COLUMN_DATE + " from " + TABLE_TRANSACTION + " order by " + COLUMN_DATE + " DESC limit 1";
        Cursor maxCur = db.rawQuery(max,null);
        if (maxCur.moveToFirst())
        {
            String maxDate = maxCur.getString(0);
            String maxYear = maxDate.substring(0,4);
            String maxMonth = maxDate.substring(5,7);

            minMax[2] = Integer.valueOf(maxYear);;
            minMax[3] = Integer.valueOf(maxMonth);;
        }
        maxCur.close();

        return minMax;
    }


    ////////////////////////

    public boolean passIsEnabled()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select " + COLUMN_PASSENABLED + " from " + TABLE_SETTINGS + " limit 1";

        Cursor cursor = db.rawQuery(query,null);
        boolean enabled = false;
        if (cursor.moveToFirst())
            enabled = cursor.getString(0).equals("1") ? true : false;

        cursor.close();
        return enabled;
    }

    public void setPassEnabled( boolean enable )
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues settings = new ContentValues();
        settings.put(COLUMN_PASSENABLED, enable);

        db.update(TABLE_SETTINGS, settings, null, null);
    }

    public boolean checkPassword( String password )
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select " + COLUMN_PASSWORD + " from " + TABLE_SETTINGS + " limit 1";

        Cursor cursor = db.rawQuery(query,null);
        boolean correct = false;
        if (cursor.moveToFirst())
            correct = cursor.getString(0).equals(password) ? true : false;

        cursor.close();
        return correct;
    }

    public boolean setPassword( String oldPass, String newPass )
    {
        SQLiteDatabase db = this.getWritableDatabase();

        if ( checkPassword(oldPass) )
        {
            ContentValues settings = new ContentValues();
            settings.put(COLUMN_PASSWORD, newPass);

            db.update(TABLE_SETTINGS, settings, null, null);
            return true;
        }
        return false;
    }

    public boolean locationJobIsEnabled()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select " + COLUMN_LOCATIONJOB + " from " + TABLE_SETTINGS + " limit 1";

        Cursor cursor = db.rawQuery(query,null);
        boolean enabled = false;
        if (cursor.moveToFirst())
            enabled = cursor.getString(0).equals("1") ? true : false;

        cursor.close();
        return enabled;
    }

    public void setLocationJobEnabled( boolean enable )
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues settings = new ContentValues();
        settings.put(COLUMN_LOCATIONJOB, enable);

        db.update(TABLE_SETTINGS, settings, null, null);
    }


    ////////////////////////////////////////////////////////////////////////

    public void addRecTransaction( RecTransaction recTransaction )
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REFTRANSACTIONID, recTransaction.getRefTransactionId());
        values.put(COLUMN_RECAMOUNT, recTransaction.getAmount());
        values.put(COLUMN_EVERYNUMBER, recTransaction.getEveryNum());
        values.put(COLUMN_EVERYUNIT, recTransaction.getEveryUnit());
        values.put(COLUMN_FORNUMBER, recTransaction.getForNum());
        values.put(COLUMN_FORUNIT, recTransaction.getForUnit());
//        values.put(COLUMN_EVERYNDAYS, recTransaction.getEveryNDays());
//        values.put(COLUMN_ENDDATE, recTransaction.getEndDate());
        values.put(COLUMN_LASTEXDATE, recTransaction.getLastExDate());

        // Inserting Row
        db.insert(TABLE_RECTRANSACTION, null, values);

    }

    public void deleteRecTransaction( RecTransaction recTransaction )
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "Delete from " + TABLE_RECTRANSACTION + " where " + COLUMN_RECID + " = " + recTransaction.getRecId();
        db.execSQL(query);
    }

    public void editRecTransaction( RecTransaction recTransaction )
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RECAMOUNT, recTransaction.getAmount());
        values.put(COLUMN_EVERYNUMBER, recTransaction.getEveryNum());
        values.put(COLUMN_EVERYUNIT, recTransaction.getEveryUnit());
        values.put(COLUMN_FORNUMBER, recTransaction.getForNum());
        values.put(COLUMN_FORUNIT, recTransaction.getForUnit());
        values.put(COLUMN_LASTEXDATE, recTransaction.getLastExDate());;

        // updating row
        db.update(TABLE_RECTRANSACTION, values, COLUMN_RECID + " = ?",
                new String[] { String.valueOf(recTransaction.getRecId()) });

    }

    public RecTransaction getRecTransaction( int id )
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * from " + TABLE_RECTRANSACTION + " where " + COLUMN_RECID + " = " + id;

        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            RecTransaction recTransaction = new RecTransaction(cursor);
            cursor.close();
            return recTransaction;
        }
        cursor.close();
        return null;
    }

    public List<RecTransaction> getRecTransactions()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * from " + TABLE_RECTRANSACTION;

        Cursor cursor = db.rawQuery(query,null);
        ArrayList<RecTransaction> recTransactions = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do
            {
                recTransactions.add(new RecTransaction(cursor));

            } while ( cursor.moveToNext());
        }
        cursor.close();
        return recTransactions;
    }

    public int getRecTransactionsCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT count(" + COLUMN_RECID + ") FROM " + TABLE_RECTRANSACTION;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = Integer.parseInt(cursor.getString(0));
        cursor.close();
        return count;
    }


}
