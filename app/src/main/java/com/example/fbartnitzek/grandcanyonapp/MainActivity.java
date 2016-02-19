package com.example.fbartnitzek.grandcanyonapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private static final LatLng LAT_LNG_GRAND_CANYON = new LatLng(36.0579667, -112.1430996);
    private static final LatLng LAT_LNG_COLOSSEUM = new LatLng(41.8900487, 12.4926753);
    private static final LatLng LAT_LNG_STONEHENGE = new LatLng(51.1788898, -1.8262146);
    private static final LatLng LAT_LNG_UDACITY = new LatLng(37.400546, -122.108668);

    private static final List<String> locationNames = new ArrayList<>();
    private static final List<LatLng> locations = new ArrayList<>();

    int mPosition = 0;
    boolean mViewLoaded = false;
    StreetViewPanorama mPanorama;
    Button mNext;
    Button mPrevious;
    TextView mLocation;

    private static final String LOG_TAG = MainActivity.class.getName();

    static {
        locations.add(LAT_LNG_GRAND_CANYON);
        locationNames.add("Grand Canyon");
        locations.add(LAT_LNG_COLOSSEUM);
        locationNames.add("Rom Colosseum");
        locations.add(LAT_LNG_STONEHENGE);
        locationNames.add("Stonehenge");
        locations.add(LAT_LNG_UDACITY);
        locationNames.add("Udacity");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrevious = (Button) findViewById(R.id.btnPrevious);
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition--;
                if (mPosition < 0) {
                    mPosition = locations.size() - 1;
                }
                Log.v(LOG_TAG, "onClick previous, position changed to " + mPosition);
                updatePanorama();

            }
        });

        mLocation = (TextView) findViewById(R.id.location);

        mNext = (Button) findViewById(R.id.btnNext);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition++;
                if (mPosition >= locations.size()) {
                    mPosition = 0;
                }
                Log.v(LOG_TAG, "onClick next, position changed to " + mPosition);
                updatePanorama();
            }
        });

        StreetViewPanoramaFragment streetViewFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewFragment.getStreetViewPanoramaAsync(this);
    }

    private void updatePanorama() {
        Log.v(LOG_TAG, "updatePanorama to " + locationNames.get(mPosition));

        if (mPanorama != null && mViewLoaded) {
            updateViews();
            mPanorama.setPosition(locations.get(mPosition));
        }

    }

    private void updateViews() {
        if (mPosition > 1) {
            mPrevious.setText(locationNames.get(mPosition - 1));
        } else {
            mPrevious.setText(locationNames.get(locationNames.size() - 1));
        }

        mLocation.setText(locationNames.get(mPosition));

        if (mPosition < locationNames.size() - 1) {
            mNext.setText(locationNames.get(mPosition + 1));
        } else {
            mNext.setText(locationNames.get(0));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        Log.v(LOG_TAG, "onStreetViewPanoramaReady, locationName:" + locationNames.get(mPosition)
                + ", streetViewPanorama = [" + streetViewPanorama + "]");
        mViewLoaded = true;
        mPanorama = streetViewPanorama;
        mPanorama.setPosition(locations.get(mPosition));
        updateViews();
        StreetViewPanoramaCamera pc = StreetViewPanoramaCamera.builder()
                .bearing(180).build();
        mPanorama.animateTo(pc, 1000);
    }
}
