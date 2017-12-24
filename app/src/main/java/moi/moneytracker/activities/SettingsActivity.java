package moi.moneytracker.activities;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.evernote.android.job.JobManager;

import moi.moneytracker.BkgJobCreator;
import moi.moneytracker.BkgLocationJob;
import moi.moneytracker.BkgPassNotifyJob;
import moi.moneytracker.fragments.CreatePasswordFragment;
import moi.moneytracker.DatabaseHandler;
import moi.moneytracker.fragments.EditPasswordFragment;
import moi.moneytracker.fragments.EnablePasswordFragment;
import moi.moneytracker.MTApp;
import moi.moneytracker.R;

public class SettingsActivity extends AppCompatActivity {


    Toolbar toolbar;
    Button editPassBtn;
    CheckBox passChk;
    CheckBox locationJobChk;

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = MTApp.getDatabase();

        toolbar = (Toolbar) findViewById(R.id.settingsTB);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.settings);

        editPassBtn = (Button) findViewById(R.id.editPassBtn);
        if ( db.checkPassword("") )
            editPassBtn.setEnabled(false);

        editPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment df = new EditPasswordFragment();
                df.show(getSupportFragmentManager(),"editPassword");
            }
        });

        passChk = (CheckBox) findViewById(R.id.passChk);
        passChk.setChecked(db.passIsEnabled());
        passChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passChk.setChecked(!passChk.isChecked());

                if (db.checkPassword(""))
                {
                    DialogFragment df = new CreatePasswordFragment();
                    df.show(getSupportFragmentManager(),"createPassword");
                }
                else
                {
                    DialogFragment df = new EnablePasswordFragment();
                    ((EnablePasswordFragment)df).setEnabling(!passChk.isChecked());
                    df.show(getSupportFragmentManager(),"enableDisablePassword");
                }
            }
        });


        locationJobChk = (CheckBox) findViewById(R.id.locationJobChk);
        locationJobChk.setChecked(db.locationJobIsEnabled());
        locationJobChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                db.setLocationJobEnabled(b);
                JobManager manager = ((MTApp) getApplication()).getJobManager();
                if ( b && db.getMapLocationsCount() > 0 )
                {
                    manager.addJobCreator(new BkgJobCreator(BkgLocationJob.TAG, getApplicationContext()));
                    manager.addJobCreator(new BkgJobCreator(BkgPassNotifyJob.TAG, getApplicationContext()));
                }
                else
                {
                    manager.cancelAllForTag(BkgLocationJob.TAG);
                    manager.cancelAllForTag(BkgPassNotifyJob.TAG);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void changePassword(String oldPass ,String newPass)
    {
        db.setPassword(oldPass,newPass);
    }

    public boolean checkDbPass( String pass)
    {
        return db.checkPassword(pass);
    }

    public void enableDisablePassword(boolean enable)
    {
        passChk.setChecked(enable);
        db.setPassEnabled(enable);
    }

    public void setPassword(String oldPass, String newPass)
    {
        passChk.setChecked(true);
        editPassBtn.setEnabled(true);
        db.setPassEnabled(true);
        db.setPassword(oldPass,newPass);
    }
}
