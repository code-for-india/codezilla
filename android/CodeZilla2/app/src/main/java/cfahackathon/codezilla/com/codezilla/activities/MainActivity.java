package cfahackathon.codezilla.com.codezilla.activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
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

import cfahackathon.codezilla.com.codezilla.R;
import cfahackathon.codezilla.com.codezilla.extras.ZAnimatorListener;
import cfahackathon.codezilla.com.codezilla.fragments.MainActivityMapFragment;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CHECK_SETTINGS = 50;
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

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

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

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
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

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                // getting last known location
                Log.w("code", "Latitude LastKnown" + String.valueOf(mLastLocation.getLatitude()));
                Log.w("code", "Longitude LastKnown" + String.valueOf(mLastLocation.getLongitude()));
            }
        } catch (Exception e) {

        }

        createLocationRequest();
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }

            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
