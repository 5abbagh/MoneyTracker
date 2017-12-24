package moi.moneytracker.models;

import android.database.Cursor;

/**
 * Created by Ali on 01-Dec-17.
 */

public class RecTransaction
{
    private int RecId;
    private int RefTransactionId;
    private double RecAmount;

    private int EveryNum;
    private String EveryUnit;
    private int ForNum;
    private String ForUnit;

//    private int EveryNDays;
//    private String EndDate;
    private String LastExDate;


    public RecTransaction(){

    }

    public RecTransaction( int id, int ref, double amount, int every, String everyU, int forNum, String forU, String last )
    {
        RecId = id;
        RefTransactionId = ref;
        RecAmount = amount;
        EveryNum = every;
        EveryUnit = everyU;
        ForNum = forNum;
        ForUnit = forU;
        LastExDate = last;
    }

    public RecTransaction( int ref, double amount, int every, String everyU, int forNum, String forU, String last )
    {
        RefTransactionId = ref;
        RecAmount = amount;
        EveryNum = every;
        EveryUnit = everyU;
        ForNum = forNum;
        ForUnit = forU;
        LastExDate = last;
    }

    public RecTransaction( Cursor cursor )
    {

        RecId = Integer.valueOf(cursor.getString(0));
        RefTransactionId = Integer.valueOf(cursor.getString(1));
        RecAmount = Double.valueOf(cursor.getString(2));
        EveryNum = Integer.valueOf(cursor.getString(3));
        EveryUnit = cursor.getString(4);
        ForNum = Integer.valueOf(cursor.getString(5));
        ForUnit = cursor.getString(6);
        LastExDate = cursor.getString(7);
    }


    public int getRecId() {
        return RecId;
    }

    public int getRefTransactionId() {
        return RefTransactionId;
    }

    public double getAmount() {
        return RecAmount;
    }

    public void setAmount(double amount){ RecAmount = amount; }

    public int getEveryNum(){ return EveryNum;}

    public int getForNum(){ return ForNum;}

    public String getEveryUnit(){ return EveryUnit;}

    public String getForUnit(){ return ForUnit;}

    public void setEveryNum(int e){ EveryNum = e; }
    public void setEveryUnit(String eu){ EveryUnit = eu; }
    public void setForNum(int forN){ ForNum = forN; }
    public void setForUnit(String forU){ ForUnit = forU; }

    public void setRefTransactionId(int ref ){ RefTransactionId = ref; }

    public void setLastExDate(String last ){ LastExDate = last; }

//    public int getEveryNDays() {
//        return EveryNDays;
//    }
//
//    public void setEveryNDays(int period){ EveryNDays = period; }
//
//    public String getEndDate() {
//        return EndDate;
//    }
//
//    public void setEndDate(String end) {EndDate = end;}

    public String getLastExDate() {
        return LastExDate;
    }
}
