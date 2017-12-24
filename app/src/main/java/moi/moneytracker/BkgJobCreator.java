package moi.moneytracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by Ali on 16-Nov-17.
 */

public class BkgJobCreator implements JobCreator
{

    Context context;
    public BkgJobCreator( String tag, Context cnt )
    {
        super();
        context = cnt;
        create(tag);
    }

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case BkgLocationJob.TAG:
                return new BkgLocationJob(context);
            case BkgPassNotifyJob.TAG:
                return new BkgPassNotifyJob(context);
            case BkgRecTransactionsJob.TAG:
                return new BkgRecTransactionsJob(context);
            default:
                return null;
        }
    }
}
