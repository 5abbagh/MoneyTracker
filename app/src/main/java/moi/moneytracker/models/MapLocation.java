package moi.moneytracker.models;

import android.database.Cursor;

/**
 * Created by Ali on 15-Nov-17.
 */

public class MapLocation
{
    private int LocId;
    private String LocName;
    private double Lat;
    private double Lng;

    public MapLocation( int id, String name, double latit, double lang)
    {
        LocId = id;
        LocName = name;
        Lat = latit;
        Lng = lang;
    }

    public MapLocation( String name, double latit, double lang)
    {
        LocName = name;
        Lat = latit;
        Lng = lang;
    }

    public MapLocation(Cursor cursor)
    {
        LocId = Integer.valueOf(cursor.getString(0));
        LocName = cursor.getString(1);
        Lat = Double.valueOf(cursor.getString(2));
        Lng = Double.valueOf(cursor.getString(3));
    }

    public int getLocationId(){ return LocId; }

    public String getName(){ return LocName;}

    public double getLat(){ return Lat; }

    public double getLng(){ return Lng; }

}
