package cfahackathon.codezilla.com.codezilla.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cfahackathon.codezilla.com.codezilla.R;
import cfahackathon.codezilla.com.codezilla.activities.MainActivity;
import cfahackathon.codezilla.com.codezilla.objects.HomeObject;
import cfahackathon.codezilla.com.codezilla.objects.HomeObjectsList;

/**
 * Created by ashis_000 on 3/19/2016.
 */
public class MainActivityMapFragment extends BaseFragment implements OnMapReadyCallback {

    GoogleMap map;

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
    public void onMapReady(GoogleMap mapCurr) {
        map = mapCurr;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.addMarker(new MarkerOptions().position(new LatLng(28.6139, 77.2090)).title("Marker"));
    }

    public void fillData(HomeObjectsList mData,Location location) {
        LatLng sydney = new LatLng(location.getLatitude(),location.getLongitude());
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        for (HomeObject obj : mData.getFeed()) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(obj.getLatitude(), obj.getLongitude()))
                    .title(obj.getLocation_string()));
        }
    }
}
