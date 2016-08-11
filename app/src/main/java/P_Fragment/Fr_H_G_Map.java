package P_Fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pyojihye.airpollution.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 2016-08-03.
 */
public class Fr_H_G_Map extends Fragment implements OnMapReadyCallback {
    @Nullable
    LatLng latLng;
    public static GoogleMap hgMap = null;
    //MapFragment

    View view;
    public Fr_H_G_Map(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map2, container, false);

        //MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map);
        //map.getMapAsync(this);
        //return view;
        //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map3);
        //MapFragment map=(MapFragment)getChildFragmentManager().findFragmentById(R.id.map3);
        //MapFragment map=(MapFragment)getChildFragmentManager().findFragmentByTag("map3");
        //MapFragment map=(MapFragment)getChildFragmentManager().findFragmentByTag("map3");
        MapFragment map = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map4);
        map.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        hgMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        //googleMap.addMarker(new MarkerOptions().position(latLng).draggable(false));


        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
        //googleMap.setOnMarkerClickListener(this);
        //googleMap.setOnCircleClickListener(this);
        //googleMap.setOnMapClickListener(this);
        hgMap.getProjection().getVisibleRegion();
    }
}
