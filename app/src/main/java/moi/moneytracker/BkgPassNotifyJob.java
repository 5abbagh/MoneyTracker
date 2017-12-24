package moi.moneytracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import moi.moneytracker.activities.PassBysActivity;

/**
 * Created by Ali on 18-Nov-17.
 */

public class BkgPassNotifyJob extends DailyJob {

    public static final String TAG = "MoneyTrackerNotificationService";
    private final int NOTIFICATIONID = 22 ;

    private Context context;
    private DatabaseHandler db;

    public BkgPassNotifyJob(Context context)
    {
        super();
        this.context = context;
        scheduleDailyJob();
    }

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(Params params) {

        Log.d("xyz:","in onRunDailyJob");
        db = MTApp.getDatabase();
        int count = db.getPassBysCount();
        if ( count > 0)
        {

            Intent resultIntent = new Intent(getContext(), PassBysActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
            stackBuilder.addNextIntentWithParentStack(resultIntent);

            PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

//            PendingIntent pi = PendingIntent.getActivity(getContext(), 0,
//                    new Intent(getContext(), PassBysActivity.class), 0);

            Notification notification = new NotificationCompat.Builder(getContext())
                    .setContentTitle(getContext().getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentText(getContext().getString(R.string.youPassedBy)  + " " + count + " " +getContext().getString(R.string.anySpends))
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .build();

            NotificationManagerCompat.from(getContext())
                    .notify(NOTIFICATIONID, notification);
        }


        return DailyJobResult.SUCCESS;
    }

    private void scheduleDailyJob() {

        if (!JobManager.instance().getAllJobRequestsForTag(TAG).isEmpty())
        {
            Log.d("xyz:","no need to schedule daily");
            return;
        }


        DailyJob.schedule(new JobRequest.Builder(TAG), TimeUnit.HOURS.toMillis(19), TimeUnit.HOURS.toMillis(20));

        Log.d("xyz:","DailyJob is scheduled");
//        DailyJob.schedule(new JobRequest.Builder(TAG), TimeUnit.MINUTES.toMillis(733), TimeUnit.MINUTES.toMillis(735));
    }

}
