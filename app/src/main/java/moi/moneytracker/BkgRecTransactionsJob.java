package moi.moneytracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import moi.moneytracker.models.RecTransaction;
import moi.moneytracker.models.Transaction;

/**
 * Created by Ali on 05-Dec-17.
 */

public class BkgRecTransactionsJob extends DailyJob
{
    public static final String TAG = "MoneyTrackerRecTransactionsService";

    private Context context;
    private DatabaseHandler db;
    private ArrayList<RecTransaction> recTransactions;

    public BkgRecTransactionsJob(Context context)
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
        recTransactions = (ArrayList<RecTransaction>) db.getRecTransactions();

        for( RecTransaction recTransaction : recTransactions )
        {
            Log.d("xyz:","checking next recTransaction");
            Transaction originalTran = db.getTransaction(recTransaction.getRefTransactionId());
            String transactionOriginalDate = originalTran.getDate();

            boolean timeElapsed = timeElapsed(recTransaction.getEveryNum(), recTransaction.getEveryUnit()
                    ,recTransaction.getLastExDate());

            Log.d("xyz:","timeElapsed : " + timeElapsed);
            DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime original = dateStringFormat.parseDateTime(transactionOriginalDate);
            DateTime endDate;
            if ( recTransaction.getForNum() != 0 )
            {
                switch ( recTransaction.getForUnit())
                {
                    case "Day":
                        endDate = original.plusDays(recTransaction.getForNum());

                    case "Month":
                        endDate = original.plusMonths(recTransaction.getForNum());

                    case "Year":
                        endDate = original.plusYears(recTransaction.getForNum());

                    default: endDate = new DateTime();
                }
            }
            else
                endDate = new DateTime().plusDays(1);

            boolean endDateNotReached = endDateNotReached( endDate.toString("yyyy-MM-dd"));

            Log.d("xyz:","endDateNotReached : " + endDateNotReached);
            Log.d("xyz:","date now is : " + new DateTime().toString("yyyy-MM-dd"));
            if ( timeElapsed && endDateNotReached )
            {
                // add transaction
                Transaction toAdd = new Transaction();
                toAdd.setRefAccountId(originalTran.getRefAccountId());
                toAdd.setNotes(originalTran.getNotes());
                toAdd.setType(originalTran.getType());
                toAdd.setAmount(recTransaction.getAmount());
                toAdd.setCategory(originalTran.getCategory());
                toAdd.setDate(new DateTime().toString("yyyy-MM-dd"));

                db.addTransaction(toAdd);

                Log.d("xyz:","transaction added");
                // updated lastExDate
                recTransaction.setLastExDate(new DateTime().toString("yyyy-MM-dd"));
                db.editRecTransaction(recTransaction);
                Log.d("xyz:","recTransaction edited");
            }
            if ( ! endDateNotReached )
            {
                db.deleteRecTransaction(recTransaction);
                Log.d("xyz:","recTransaction deleted");
            }
        }

        return DailyJobResult.SUCCESS;
    }

    private boolean timeElapsed( int freq, String unit, String lastExDate )
    {

        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime firstTime = dateStringFormat.parseDateTime(lastExDate);
        DateTime secondTime = dateStringFormat.parseDateTime(new DateTime().toString("yyyy-MM-dd"));

        switch (unit)
        {
            case "Day":
            {
                int days = Days.daysBetween(new LocalDate(firstTime), new LocalDate(secondTime)).getDays();
                return days >= freq;
            }
            case "Month":
            {
                int months = Months.monthsBetween(new LocalDate(firstTime), new LocalDate(secondTime)).getMonths();
                return months >= freq;
            }
            case "Year":
            {
                int years = Years.yearsBetween(new LocalDate(firstTime), new LocalDate(secondTime)).getYears();
                return years >= freq;
            }
            default:
                return false;
        }
    }

    private boolean endDateNotReached( String endDate )
    {
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime firstTime = dateStringFormat.parseDateTime(endDate);
        DateTime secondTime = dateStringFormat.parseDateTime(new DateTime().toString("yyyy-MM-dd"));

        int days = Days.daysBetween(new LocalDate(firstTime), new LocalDate(secondTime)).getDays();

        return days < 0;
    }

    private void scheduleDailyJob() {

        if (!JobManager.instance().getAllJobRequestsForTag(TAG).isEmpty())
        {
            Log.d("xyz:","no need to schedule daily recTransaction");
            return;
        }


        DailyJob.schedule(new JobRequest.Builder(TAG), TimeUnit.HOURS.toMillis(12), TimeUnit.HOURS.toMillis(13));

        Log.d("xyz:","DailyJob is scheduled");
//        DailyJob.schedule(new JobRequest.Builder(TAG), TimeUnit.MINUTES.toMillis(733), TimeUnit.MINUTES.toMillis(735));
    }

}
