package moi.moneytracker.activities;

import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import com.evernote.android.job.JobManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

import moi.moneytracker.BkgJobCreator;
import moi.moneytracker.BkgLocationJob;
import moi.moneytracker.BkgPassNotifyJob;
import moi.moneytracker.DatabaseHandler;
import moi.moneytracker.MTApp;
import moi.moneytracker.MyLocationDetector;
import moi.moneytracker.MyLocationDetectorListener;
import moi.moneytracker.R;
import moi.moneytracker.fragments.AddMapLocationFragment;
import moi.moneytracker.fragments.AlertFragment;
import moi.moneytracker.models.MapLocation;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener,
                                                            OnMapReadyCallback, AlertFragment.AlertDialogListener,
                                    GoogleMap.OnMyLocationButtonClickListener, MyLocationDetectorListener {

    private GoogleMap mMap;
    private DatabaseHandler db;
    private ArrayList<MapLocation> locations;
    boolean justStarted;
    private MyLocationDetector locationDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        justStarted = true;
        db = MTApp.getDatabase();
        locationDetector = new MyLocationDetector(this,this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!justStarted) {
            locationDetector.detectLocation();
        } else {
            justStarted = false;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);

        try {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
        locationDetector.detectLocation();

        addMarkers();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        int tag = Integer.valueOf( marker.getTag().toString());
        if (tag == -1)
        {
            onMapLongClick(marker.getPosition());
        }
        else
        {
            MapLocation location = db.getMapLocation(tag);
            DialogFragment df = new AlertFragment();
            ((AlertFragment)df).setMessage(getString(R.string.delLocAlertMsg));
            ((AlertFragment)df).setToDeleteObject(location);
            df.show(getSupportFragmentManager(),"deleteLocation");
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {
        DialogFragment df = new AddMapLocationFragment();
        ((AddMapLocationFragment)df).setLat(latLng.latitude);
        ((AddMapLocationFragment)df).setLng(latLng.longitude);
        df.show(getSupportFragmentManager(),"addMapLocation");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object toDelete) {
        if(toDelete instanceof MapLocation)
        {
            db.deleteMapLocation((MapLocation) toDelete);
            mMap.clear();
            addMarkers();
            if (db.getMapLocationsCount() == 0)
            {
                JobManager manager = ((MTApp) getApplication()).getJobManager();
                manager.cancelAllForTag(BkgLocationJob.TAG);
                manager.cancelAllForTag(BkgPassNotifyJob.TAG);
                Log.d("xyz:","all jobs canceled");
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, Object toDelete) {

    }

    private void addMarkers()
    {
        locations = (ArrayList<MapLocation>) db.getMapLocations();
        for ( MapLocation location : locations )
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLat(), location.getLng())).title("- " + location.getName()))
                    .setTag(location.getLocationId());
        }
    }

    public void addMapLocation( MapLocation location)
    {
        db.addMapLocation(location);
        mMap.clear();
        addMarkers();

        JobManager manager = ((MTApp) getApplication()).getJobManager();
        manager.addJobCreator(new BkgJobCreator(BkgLocationJob.TAG, getApplicationContext()));
        manager.addJobCreator(new BkgJobCreator(BkgPassNotifyJob.TAG, getApplicationContext()));
        Log.d("xyz:","added jobs from MapsActivity");

    }

    @Override
    public boolean onMyLocationButtonClick() {
        locationDetector.detectLocation();
        return false;
    }

    @Override
    public void locationDetected( Location location)
    {
        LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,14.3f));
    }

}
