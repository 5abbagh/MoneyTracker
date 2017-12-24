package moi.moneytracker.models;


import android.database.Cursor;

/**
 * Created by Ali on 01-Nov-17.
 */

public class Account
{

    private int AccountId;
    private String AcName;
    private String AcCurrency;
    private double AcBalance;

    public Account(){}

    public Account( int id, String name, String cur, double bal)
    {
        AccountId = id;
        AcName = name;
        AcCurrency = cur;
        AcBalance = bal;
    }

    public Account( String name, String cur, double bal)
    {
        AcName = name;
        AcCurrency = cur;
        AcBalance = bal;
    }

    public Account( Cursor cursor )
    {
        AccountId = Integer.valueOf(cursor.getString(0));
        AcName = cursor.getString(1);
        AcCurrency = cursor.getString(2);
        AcBalance = Double.valueOf(cursor.getString(3));
    }


    // getting ID
    public int getAccountId(){
        return AccountId;
    }

    // setting id
    public void setAccountId(int id){
        AccountId = id;
    }



    // getting Currency
    public String getCurrency(){
        return AcCurrency;
    }

    // setting Currency
    public void setCurrency(String cur){
        AcCurrency = cur;
    }



    // getting Name
    public String getAccountName(){
        return AcName;
    }

    // setting Name
    public void setAccountName(String name){
        AcName = name;
    }


    // getting Balance
    public double getBalance(){
        return AcBalance;
    }

    // setting Balance
    public void setBalance(double bal){
        AcBalance = bal;
    }


}
