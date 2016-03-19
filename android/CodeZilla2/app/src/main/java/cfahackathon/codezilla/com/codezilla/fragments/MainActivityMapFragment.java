package cfahackathon.codezilla.com.codezilla.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cfahackathon.codezilla.com.codezilla.R;
import cfahackathon.codezilla.com.codezilla.activities.MainActivity;

/**
 * Created by ashis_000 on 3/19/2016.
 */
public class MainActivityMapFragment extends BaseFragment implements OnMapReadyCallback {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_activity_map_fragment_layout, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public static Fragment newInstance(Bundle bundle) {
        MainActivityMapFragment fragment = new MainActivityMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.addMarker(new MarkerOptions().position(new LatLng(28.6139, 77.2090)).title("Marker"));
    }
}
