package moi.moneytracker.models;

import android.database.Cursor;

/**
 * Created by Ali on 17-Nov-17.
 */

public class PassBy
{
    private int PassId;
    private int RefMapLocation;
    private String PassDate;

    public PassBy( int id, int ref, String date)
    {
        PassId = id;
        RefMapLocation = ref;
        PassDate = date;
    }

    public PassBy( int ref, String date)
    {
        RefMapLocation = ref;
        PassDate = date;
    }

    public PassBy(Cursor cursor)
    {
        PassId = Integer.valueOf(cursor.getString(0));
        RefMapLocation = Integer.valueOf(cursor.getString(1));
        PassDate = cursor.getString(2);
    }

    public int getPassId(){ return PassId; }

    public String getPassDate(){ return PassDate;}

    public int getRefMapLocation(){ return RefMapLocation; }
}
