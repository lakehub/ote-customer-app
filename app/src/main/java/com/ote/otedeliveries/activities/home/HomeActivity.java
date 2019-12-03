package com.ote.otedeliveries.activities.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ote.otedeliveries.R;
import com.ote.otedeliveries.activities.account.ProfileActivity;
import com.ote.otedeliveries.activities.order.SearchDestinationActivity;
import com.ote.otedeliveries.activities.startup.LoginActivity;
import com.ote.otedeliveries.app.Config;
import com.ote.otedeliveries.models.Login;
import com.ote.otedeliveries.models.User;
import com.ote.otedeliveries.sync.SyncUtils;
import com.ote.otedeliveries.utils.NotificationUtils;
import com.ote.otedeliveries.utils.PrefManager;
import com.ote.otedeliveries.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view) NavigationView nav_view;
    @BindView(R.id.drawer_layout) DrawerLayout drawer_layout;
    @BindView(R.id.mapSection) View mapSection;
    @BindView(R.id.loadingProgressDialog) ProgressBar loadingProgressDialog;
    @BindView(R.id.progressSection) View progressSection;

    GoogleMap googleMap;


    FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String currentUserAddress;

    // update very 10 seconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fast update very 5 seconds
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // location toggler
    private Boolean mRequestingLocationUpdates;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    SupportMapFragment mapFragment;

    private BroadcastReceiver broadcastReceiver;

    Realm realm;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
        getSupportActionBar().setTitle(getString(R.string.on_demand_delivery));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.ic_hamburger);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.oteMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Dexter.withActivity(HomeActivity.this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            updateLocationUI();

                            mRequestingLocationUpdates = true;
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        restoreValuesFromBundle(savedInstanceState);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if(intent.getAction() != null && intent.getAction().equals(Config.REGISTRATION_COMPLETE)){
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    SyncUtils.TriggerRefresh();
                }
                else if(intent.getAction() != null && intent.getAction().equals(Config.PUSH_NOTIFICATION)){
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        realm = Utils.getRealmInstance();
        user = realm.where(User.class).findFirst();

        String profilePicture = PrefManager.getProfilePicture();

        View headerView = nav_view.getHeaderView(0);

        if(!TextUtils.isEmpty(profilePicture)) {
            byte[] decodedFrontPhotoByteArray = Base64.decode(profilePicture, Base64.DEFAULT);
            Bitmap profilePhotoBitmap = BitmapFactory.decodeByteArray(decodedFrontPhotoByteArray, 0, decodedFrontPhotoByteArray.length);

            CircularImageView profile_image = headerView.findViewById(R.id.profile_image);

            Glide.with(this)
                    .load(profilePhotoBitmap)
                    .into(profile_image);

            profile_image.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }

        String userName = user.getFirstname() + " " + user.getLastname();

        AppCompatTextView profileName = headerView.findViewById(R.id.profile_name);
        profileName.setText(userName);
    }

    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            if (savedInstanceState.containsKey("is_requesting_updates"))
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");

            if (savedInstanceState.containsKey("last_known_location"))
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
        }

        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null && googleMap != null) {
            CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 15);
            googleMap.animateCamera(cameraUpdateFactory);
            googleMap.moveCamera(cameraUpdateFactory);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check for the integer request code originally supplied to startResolutionForResult().
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.e(HomeActivity.class.getCanonicalName(), "User agreed to make required location settings changes.");
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e(HomeActivity.class.getCanonicalName(), "User chose not to make required location settings changes.");
                    mRequestingLocationUpdates = false;
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        NotificationUtils.Companion.clearNotifications(getApplicationContext());
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationProviderClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_account){
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
        else if(id == R.id.action_logout){
            Login login = PrefManager.getLoginModel();
            login.setLoggedin(false);

            PrefManager.setLoginModel(login);

            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(checkPermissions())
            googleMap.setMyLocationEnabled(true);

        // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-0.091702, 34.767956), 10));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                googleMap.moveCamera(center);
                googleMap.animateCamera(center);
                googleMap.animateCamera(zoom);

                geocodeAddress(location);

                // String locationString = Location.convert(location,1);
                // Toast.makeText(HomeActivity.this, locationString, Toast.LENGTH_LONG).show();

                Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.ENGLISH);

                try{
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if(addresses != null){
                        Address returnedAddress = addresses.get(0);
                        for(int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++)
                            currentUserAddress = returnedAddress.getAddressLine(i);

                    }
                }
                catch(Exception e){
                    Log.e(HomeActivity.class.getCanonicalName(), e.getMessage());
                }

                mapSection.setVisibility(View.VISIBLE);

                loadingProgressDialog.setVisibility(View.GONE);
                progressSection.setVisibility(View.GONE);
            }
        });

        View view = ((View) Objects.requireNonNull(mapFragment.getView()).findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) view.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private void geocodeAddress(Location location){
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null){
                Address returnedAddress = addresses.get(0);
                for(int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++)
                    currentUserAddress = returnedAddress.getAddressLine(i);

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.searchAddress)
    public void searchAddressButton(){
        Intent searchIntent = new Intent(HomeActivity.this, SearchDestinationActivity.class);
        searchIntent.putExtra("clientAddress", currentUserAddress);

        startActivity(searchIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
