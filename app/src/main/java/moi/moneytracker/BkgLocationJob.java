package moi.moneytracker;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import moi.moneytracker.models.MapLocation;
import moi.moneytracker.models.PassBy;

/**
 * Created by Ali on 16-Nov-17.
 */

public class BkgLocationJob extends Job implements MyLocationDetectorListener
{
    public static final String TAG = "MoneyTrackerLocationService";
    private final double RADIUS = 100;
    private final int MIN_HOURS = 1;
    private MyLocationDetector locationDetector;
    private DatabaseHandler db;
    private Context context;
    private Handler mUserLocationHandler = null;

    public BkgLocationJob(Context context)
    {
        super();
        this.context = context;
        scheduleAdvancedJob();
    }

    @Override
    @NonNull
    protected Result onRunJob(Params params) {

        Log.d("xyz:","in onRunJob");

        locationDetector = new MyLocationDetector(context,this);
        db = MTApp.getDatabase();

        Looper.prepare();
        mUserLocationHandler = new Handler();
        locationDetector.detectLocation();
        Looper.loop();

        return Result.SUCCESS;
    }

    private void scheduleAdvancedJob() {


        if (!JobManager.instance().getAllJobRequestsForTag(TAG).isEmpty())
        {
            Log.d("xyz:","no need to schedule periodic");
            return;
        }


//        PersistableBundleCompat extras = new PersistableBundleCompat();
//        extras.putString("key", "Hello world");

        int jobId = new JobRequest.Builder(BkgLocationJob.TAG)
//                .setExecutionWindow(30_000L, 40_000L)
//                .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(14))
//                .setExtras(extras)
//                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    private void cancelJob(int jobId) {
        JobManager.instance().cancel(jobId);
    }

    @Override
    public void locationDetected(Location curLocation)
    {
        if(mUserLocationHandler != null){
            mUserLocationHandler.getLooper().quit();
        }
        //Log.d("xyz:","location received");
        ArrayList<MapLocation> mapLocations = (ArrayList<MapLocation>) db.getMapLocations();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
        String timeNow = formatter.format(calendar.getTime());
        // format example: 03/25 18:05

        for ( MapLocation mapLocation : mapLocations )
        {
            Location savedLoc = new Location("");
            savedLoc.setLatitude(mapLocation.getLat());
            savedLoc.setLongitude(mapLocation.getLng());

            float distance = savedLoc.distanceTo(curLocation);

            if ( Math.abs(distance) <=  RADIUS )
            {
                // check if minimum time has elapsed for that place
                PassBy latest = db.getLatestPassByTo(mapLocation.getLocationId());
                if ( latest == null || checkTimeElapsed( latest.getPassDate(), timeNow) )
                {
                    //Log.d("xyz:","adding passby");
                    PassBy pass = new PassBy(mapLocation.getLocationId(),timeNow);
                    db.addPassBy(pass);
                }
//                else
                    //Log.d("xyz:","time hasn't elapsed");
            }
        }
    }

    private boolean checkTimeElapsed(String eventTime, String now )
    {
        // format example: 03/25 18:05
        int eventMonth = Integer.valueOf(eventTime.substring(0,2));
        int eventDay = Integer.valueOf(eventTime.substring(3,5));
        int eventHour = Integer.valueOf(eventTime.substring(6,8));

        int nowMonth = Integer.valueOf(now.substring(0,2));
        int nowDay = Integer.valueOf(now.substring(3,5));
        int nowHour = Integer.valueOf(now.substring(6,8));

        if ( nowMonth == eventMonth )
        {
            if (nowDay == eventDay )
            {
                if ( nowHour - eventHour > MIN_HOURS )
                    return true;
                else
                    return false;
            }
            else if ( nowDay > eventDay )
            {
                return true;
            }
            else
                return false;
        }
        else if( nowMonth > eventMonth )
            return true;
        else
            return false;
    }

}
