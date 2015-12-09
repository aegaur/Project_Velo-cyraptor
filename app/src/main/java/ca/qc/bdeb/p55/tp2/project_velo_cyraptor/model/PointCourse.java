package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gabriel on 2015-12-09.
 */
public class PointCourse {
    private double latitude;
    private double longitude;
    private double distance;

    public PointCourse(LatLng position, double distance) {
        this.latitude = position.latitude;
        this.longitude = position.longitude;
        this.distance = distance;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }
}
