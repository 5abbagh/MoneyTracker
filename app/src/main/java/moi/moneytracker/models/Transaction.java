package moi.moneytracker.models;

import android.database.Cursor;

/**
 * Created by Ali on 01-Nov-17.
 */

public class Transaction {

    // Properties
    private int TrId;
    private int RefAccountId;
    private boolean TrType;
    private String TrDate;
    private double TrAmount;
    private String TrNotes;
    private String TrCategory;
//    private boolean TrRecursive;
//    private int TrFreq;
//    private String TrFreqUnit;
//    private int TrEnd;
//    private String TrEndUnit;

    // Empty constructor
    public Transaction(){

    }

    // constructor
    public Transaction(int id, int accountId, boolean type, String date, double amount, String notes, String cat
                       ) //boolean rec, int freq, String freqUnit, int end, String endUnit
    {
        TrId = id;
        RefAccountId = accountId;
        TrType = type;
        TrDate = date;
        TrAmount = amount;
        TrNotes = notes;
        TrCategory = cat;
//        TrRecursive = rec;
//        if (rec)
//        {
//            TrFreq = freq;
//            TrFreqUnit = freqUnit;
//            TrEnd = end;
//            TrEndUnit = endUnit;
//        }
    }

    // constructor
    public Transaction( int accountId, boolean type, String date, double amount, String notes, String cat
                       ) //boolean rec, int freq, String freqUnit, int end, String endUnit
    {
        RefAccountId = accountId;
        TrType = type;
        TrDate = date;
        TrAmount = amount;
        TrNotes = notes;
        TrCategory = cat;
//        TrRecursive = rec;
//        if (rec)
//        {
//            TrFreq = freq;
//            TrFreqUnit = freqUnit;
//            TrEnd = end;
//            TrEndUnit = endUnit;
//        }
    }


    // constructor with cursor
    public Transaction(Cursor cursor)
    {
        TrId = Integer.parseInt(cursor.getString(0));
        RefAccountId = Integer.parseInt(cursor.getString(1));
        TrType = cursor.getString(2).equals("1") ? true : false;
        TrDate = cursor.getString(3);
        TrAmount = Double.parseDouble(cursor.getString(4));
        TrNotes = cursor.getString(5);
        TrCategory = cursor.getString(6);
//        TrRecursive = cursor.getString(7).equals("1") ? true : false;
//        if ( TrRecursive )
//        {
//            TrFreq = Integer.parseInt(cursor.getString(8));
//            TrFreqUnit = cursor.getString(9);
//            TrEnd = Integer.parseInt(cursor.getString(10));
//            TrEndUnit = cursor.getString(11);
//        }
    }

    // getting ID
    public int getID(){
        return TrId;
    }

    // setting id
    public void setID(int id){
        TrId = id;
    }

    // getting ID
    public int getRefAccountId(){
        return RefAccountId;
    }

    // setting id
    public void setRefAccountId(int accountId){
        RefAccountId = accountId;
    }

    // getting type
    public boolean getType(){
        return TrType;
    }

    // setting type
    public void setType(boolean type){
        TrType = type;
    }

    // getting Date
    public String getDate() { return TrDate; }

    // setting Date
    public void setDate(String date){
        TrDate = date;
    }

    // getting Amount
    public double getAmount(){
        return TrAmount;
    }

    // setting Amount
    public void setAmount(double amount){
        TrAmount = amount;
    }

    // getting Notes
    public String getNotes(){
        return TrNotes;
    }

    // setting Notes
    public void setNotes(String notes){
        TrNotes = notes;
    }

    // getting Category
    public String getCategory(){
        return TrCategory;
    }

    // setting Category
    public void setCategory(String cat){
        TrCategory = cat;
    }


    /*
    // getting recursive
    public boolean isRecursive(){
        return TrRecursive;
    }

    // setting Recursive
    public void setRecursive(boolean rec){
        TrRecursive = rec;
    }

    // getting Freq
    public int getFreq(){
        return TrFreq;
    }

    // setting Freq
    public void setFreq(int freq){
        TrFreq = freq;
    }

    // getting FreqUnit
    public String getFreqUnit(){
        return TrFreqUnit;
    }

    // setting FreqUnit
    public void setFreqUnit(String freqUnit){
        TrFreqUnit = freqUnit;
    }

    // getting End
    public int getEnd(){
        return TrEnd;
    }

    // setting End
    public void setEnd(int end){
        TrEnd = end;
    }

    // getting EndUnit
    public String getEndUnit(){
        return TrEndUnit;
    }

    // setting EndUnit
    public void setEndUnit(String endUnit){
        TrEndUnit = endUnit;
    }
*/
}
