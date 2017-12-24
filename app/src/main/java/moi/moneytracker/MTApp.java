package moi.moneytracker;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobConfig;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.JobLogger;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ali on 16-Nov-17.
 */

public class MTApp extends Application
{

    private static MTApp singleton;
    private static JobManager jobManager;
    private static DatabaseHandler db;

    @Override
    public void onCreate() {
        Log.d("xyz:","in MTApp onCreate");
        super.onCreate();
        singleton = this;
        jobManager = JobManager.create(this);
        db = DatabaseHandler.getInstance(this);

        if ( db.locationJobIsEnabled() && db.getMapLocationsCount() > 0 )
        {
            jobManager.addJobCreator(new BkgJobCreator(BkgLocationJob.TAG, getApplicationContext()));
            jobManager.addJobCreator(new BkgJobCreator(BkgPassNotifyJob.TAG, getApplicationContext()));
            Log.d("xyz:","added location jobs from MTApp");
        }
        if ( db.getRecTransactionsCount() > 0 )
        {
            jobManager.addJobCreator(new BkgJobCreator(BkgRecTransactionsJob.TAG, getApplicationContext()));
        }
    }

    public static MTApp getInstance(){
        return singleton;
    }

    public static JobManager getJobManager(){
        return jobManager;
    }

    public static Resources getLocalizedResources(Context context, Locale desiredLocale)
    {
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(desiredLocale);
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources();
    }

    public static int getIndexOf( String entry, String[] arr)
    {
        for (int i = 0; i < arr.length; i++ )
        {
            if (arr[i].equals(entry) )
                return i;
        }
        return -1;
    }

    public static DatabaseHandler getDatabase()
    {
        return db;
    }
}
