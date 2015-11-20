package ca.qc.bdeb.p55.tp2.project_velo_cyraptor;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Run extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MillisecondChronometer chronoTime;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_run_map);
        mapFragment.getMapAsync(this);

        chronoTime = (MillisecondChronometer) findViewById(R.id.activity_run_chr);
        btnStart = (Button) findViewById(R.id.activity_run_btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chronoTime.isStarted()) {
                    chronoTime.stop();
                } else {
                    chronoTime.start();
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            private boolean firstTime = true;

            @Override
            public void onMyLocationChange(Location location) {
                LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (firstTime) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 10));
                    firstTime = false;
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 10));
                }
            }
        });
        // Add a marker in Sydney and move the camera
        Location myLocation = mMap.getMyLocation();
        if (myLocation != null) {
            LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myLatLng).title("You"));

        }
    }
}
