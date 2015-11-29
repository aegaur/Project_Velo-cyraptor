package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gabriel on 2015-11-27.
 */
public class PointCourse {
    LatLng latLng;
    long timeStamp;

    public PointCourse(LatLng latLng, long timeStamp) {
        this.latLng = latLng;
        this.timeStamp = timeStamp;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
