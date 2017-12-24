package moi.moneytracker;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Ali on 17-Nov-17.
 */

public interface MyLocationDetectorListener
{
    void locationDetected(Location location);
}
