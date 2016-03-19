package cfahackathon.codezilla.com.codezilla.activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;

import java.util.HashMap;

import cfahackathon.codezilla.com.codezilla.R;
import cfahackathon.codezilla.com.codezilla.application.ZApplication;
import cfahackathon.codezilla.com.codezilla.extras.Urls;
import cfahackathon.codezilla.com.codezilla.extras.ZAnimatorListener;
import cfahackathon.codezilla.com.codezilla.fragments.MainActivityMapFragment;
import cfahackathon.codezilla.com.codezilla.objects.HomeObjectsList;
import cfahackathon.codezilla.com.codezilla.serverApi.AppRequestListener;
import cfahackathon.codezilla.com.codezilla.serverApi.CustomStringRequest;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, Urls, AppRequestListener {

    ViewPager viewPager;
    TabLayout tabLayout;
    MyPagerAdapter adapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    public static final int TRANSLATION_DURATION = 200;
    boolean isToolbarAnimRunning;
    AppBarLayout appBarLayout;

    int deviceHeight, deviceWidth;
    ProgressDialog progressDialog;
    Snackbar snackbar;

    HomeObjectsList mData;
    HashMap<Integer, Fragment> hashMap;
    Location currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        setProgressLayoutVariablesAndErrorVariables();

        deviceHeight = getResources().getDisplayMetrics().heightPixels;
        deviceWidth = getResources().getDisplayMetrics().widthPixels;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.indicator);
        viewPager = (ViewPager) findViewById(R.id.pager_launch);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CodeZilla");

        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                try {
                    toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } catch (Exception e) {
                    toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                toolbarHeight = toolbar.getHeight();
            }
        });

        setDrawerActionBarToggle();
        setDrawerItemClickListener();
        viewPager.setOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(3);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        showLoadingLayout();
        hideErrorLayout();

        requestLocation();
    }

    private void requestLocation() {
        SmartLocation.with(this).location().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                makeUseOfNewLocation(location);
            }
        });
    }

    private void makeUseOfNewLocation(Location location) {
        if(currentLocation==null){
            currentLocation = location;
            if (location != null) {
                Log.w("as", "lat : " + location.getLatitude());
                Log.w("as", "lat : " + location.getLongitude());
            }

            String url = HOME_URL + "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
            CustomStringRequest request = new CustomStringRequest(Request.Method.GET, url, HOME_URL, this, null);
            ZApplication.getInstance().addToRequestQueue(request, HOME_URL);
        }
    }

    private void setDrawerItemClickListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {

                    default:
                        return true;
                }
            }
        });
    }

    private void setDrawerActionBarToggle() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.z_open_drawer, R.string.z_close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setItemIconTintList(null);
    }

    @Override
    public void onRequestStarted(String requestTag) {

    }

    @Override
    public void onRequestFailed(String requestTag, VolleyError error) {
        hideLoadingLayout();
        showErrorLayout();
    }

    @Override
    public void onRequestCompleted(String requestTag, String response) {
        hideLoadingLayout();
        hideErrorLayout();

        try {
            mData = new Gson().fromJson(response, HomeObjectsList.class);
            fillDataInViewPager();
        } catch (Exception e) {

        }
    }

    private void fillDataInViewPager() {
        ((MainActivityMapFragment) hashMap.get(0)).fillData(mData,currentLocation);
    }


    class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Bundle bundle = new Bundle();

            Fragment fragment = null;
            if (pos == 0)
                fragment = MainActivityMapFragment.newInstance(bundle);
            else
                fragment = MainActivityMapFragment.newInstance(bundle);

            hashMap.put(pos, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Map";
            } else if (position == 1) {
                return "List";
            } else {
                return "";
            }
        }
    }

    @SuppressLint("NewApi")
    public void scrollToolbarBy(int dy) {
        float requestedTranslation = appBarLayout.getTranslationY() + dy;
        if (requestedTranslation < -toolbarHeight) {
            requestedTranslation = -toolbarHeight;
            appBarLayout.setTranslationY(requestedTranslation);
        } else if (requestedTranslation > 0) {
            requestedTranslation = 0;
            appBarLayout.setTranslationY(requestedTranslation);
        } else if (requestedTranslation >= -toolbarHeight && requestedTranslation <= 0) {
            appBarLayout.setTranslationY(requestedTranslation);
        }
    }

    @SuppressLint("NewApi")
    public void scrollToolbarAfterTouchEnds() {
        float currentTranslation = -appBarLayout.getTranslationY();
        if (currentTranslation > toolbarHeight / 2) {
            animateToolbarLayout(-toolbarHeight);
        } else {
            animateToolbarLayout(0);
        }
    }

    public void setToolbarTranslation(View firstChild) {
        if (firstChild.getTop() > appBarLayout.getHeight()) {
            animateToolbarLayout(0);
        } else {
            scrollToolbarAfterTouchEnds();
        }
    }

    public void setToolbarTranslationFromPositionOfTopChildAfterTouchEnd(int pos) {
        if (pos > appBarLayout.getHeight()) {
            animateToolbarLayout(0);
        } else {
            scrollToolbarAfterTouchEnds();
        }
    }

    void animateToolbarLayout(int trans) {
        appBarLayout.animate().translationY(trans).setDuration(TRANSLATION_DURATION)
                .setInterpolator(new DecelerateInterpolator()).setListener(new ZAnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                isToolbarAnimRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isToolbarAnimRunning = false;
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (appBarLayout.getTranslationY() != 0 && !isToolbarAnimRunning) {
            animateToolbarLayout(0);
        }
    }

    @Override
    public void onPageSelected(int arg0) {

    }
}
