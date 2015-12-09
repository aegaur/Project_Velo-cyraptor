package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.run;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.PointCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.CallbackMap;

/**
 * Created by gabriel on 2015-12-02.
 */
public class GestionnaireMap implements OnMapReadyCallback {
    private static final float ZOOM_INITIAL = 15.0f;
    private static final int INTERVALLE_SAUVEGARDE_MOUVEMENT = 5;
    private static final float LARGEUR_LIGNE = 7.5f;
    private static final int NOMBRE_RESULTATS = 3;
    private static final int NOMBRE_DE_METRES_PAR_KM = 1000;
    private static final int INDICE_RESULTAT = 0;

    private LinkedList<PointCourse> listePoints = new LinkedList<>();
    private boolean onMapAnimationFinished = false;
    private boolean firstTime = true;
    private boolean isRunning = false;
    private Polyline polyline;
    private GoogleMap map;
    private Context context;
    private CallbackMap callbackMap;
    private double distanceTotale;

    public GestionnaireMap(Context context, CallbackMap callbackMap) {
        this.context = context;
        this.callbackMap = callbackMap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        UiSettings uiSettings = map.getUiSettings();
        map.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            private int compteurSecondes = 0;

            @Override
            public void onMyLocationChange(Location location) {
                LatLng myLatLng;
                if (firstTime) {
                    firstTime = false;
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    animerCameraMap(CameraUpdateFactory.newLatLngZoom(myLatLng, ZOOM_INITIAL));
                } else if (onMapAnimationFinished && compteurSecondes
                        >= INTERVALLE_SAUVEGARDE_MOUVEMENT && isRunning) {
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    animerCameraMap(CameraUpdateFactory.newLatLng(myLatLng));
                    compteurSecondes = 0;
                    updatePath(myLatLng);
                    callbackMap.callbackMap();
                }
                compteurSecondes++;
            }
        });
    }

    private void animerCameraMap(CameraUpdate cameraUpdate) {
        map.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                onMapAnimationFinished = true;
            }

            @Override
            public void onCancel() {
                onMapAnimationFinished = true;
            }
        });
    }

    void start() {
        isRunning = true;
        if (listePoints.size() == 0) {
            Location location = map.getMyLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            listePoints.add(new PointCourse(latLng, 0));
            polyline = map.addPolyline(new PolylineOptions().add(latLng)
                    .width(LARGEUR_LIGNE)
                    .color(context.getResources().getColor(R.color.colorAccent)));
        }
    }

    void resume() {
        isRunning = true;
    }

    void pause() {
        isRunning = false;
    }

    void stop() {
        isRunning = false;
        listePoints.clear();
        polyline.remove();
    }

    void updatePath(LatLng position) {
        updateDistanceTotale();
        listePoints.add(new PointCourse(position, distanceTotale));
        polyline.getPoints().add(position);
    }

    double getDistanceTotale() {
        return distanceTotale;
    }
    
    private void updateDistanceTotale(){
        float[] results = new float[NOMBRE_RESULTATS];
        float resultat = 0.0f;

        ListIterator<PointCourse> iteratoryPoints = listePoints.listIterator();
        if (listePoints.size() > 1) {
            LatLng point1 = iteratoryPoints.next().getLatLng();
            while (iteratoryPoints.nextIndex() < listePoints.size()) {
                LatLng point2 = iteratoryPoints.next().getLatLng();
                resultat += calculerDistance(point1, point2, results);
                point1 = point2;
            }
        }

        distanceTotale = resultat;
    }

    private float calculerDistance(LatLng latLng1, LatLng latLng2, float[] results) {
        Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results);
        return results[INDICE_RESULTAT] / NOMBRE_DE_METRES_PAR_KM;
    }
}
