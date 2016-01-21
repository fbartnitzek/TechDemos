package com.example.fbartnitzek.myfirstmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final LatLng LAT_LNG_FRANKFURT = new LatLng(50.108241, 8.681988);
    private static final LatLng LAT_LNG_SEATTLE = new LatLng(47.6204, -122.3491);
    private static final LatLng LAT_LNG_TOKYO = new LatLng(35.6744071, 139.7166253);
    private static final LatLng LAT_LNG_NIAGRA_FALLS = new LatLng(43.0918241, -79.0732351);
    GoogleMap mMap;
    boolean mMapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapReady) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        Button btnSatellite = (Button) findViewById(R.id.btnSatellite);
        btnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapReady) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        Button btnHybrid = (Button) findViewById(R.id.btnHybrid);
        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapReady) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });

        Button btnTerrain = (Button) findViewById(R.id.btnTerrain);
        btnTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapReady) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
            }
        });

        Button btnFrankfurt = (Button) findViewById(R.id.btnFrankfurt);
        btnFrankfurt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flyTo(LAT_LNG_FRANKFURT);
            }
        });

        Button btnSeattle = (Button) findViewById(R.id.btnSeattle);
        btnSeattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flyTo(LAT_LNG_SEATTLE);
            }
        });

        Button btnTokyo = (Button) findViewById(R.id.btnTokyo);
        btnTokyo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flyTo(LAT_LNG_TOKYO);
            }
        });

        // get map - so it can be dynamically changed
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    public void flyTo(LatLng target) {
        if (mMapReady) {
            CameraPosition cp = CameraPosition.builder().target(target)
                    .zoom(17)
                    .bearing(new Random().nextInt(360))
                    .tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp),
                    10000, null);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapReady = true;
        mMap = googleMap;
//        LatLng latLngNewYork = new LatLng(40.7484, -73.9857);   //set center
        LatLng latLngNiagara = LAT_LNG_NIAGRA_FALLS;   //set center
        CameraPosition target = CameraPosition.builder().target(latLngNiagara)
                .zoom(18).bearing(190).tilt(67).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }
}
