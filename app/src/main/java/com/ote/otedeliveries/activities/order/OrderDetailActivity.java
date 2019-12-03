package com.ote.otedeliveries.activities.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ote.otedeliveries.R;
import com.ote.otedeliveries.activities.account.ProfileActivity;
import com.ote.otedeliveries.activities.home.HomeActivity;
import com.ote.otedeliveries.databinding.ActivityOrderDetailBinding;
import com.ote.otedeliveries.models.Order;
import com.ote.otedeliveries.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    Realm realm;
    Order order;

    GoogleMap googleMap;
    SupportMapFragment mapFragment;

    @BindView(R.id.order_date) AppCompatTextView order_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOrderDetailBinding activityOrderDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        ButterKnife.bind(this);

        realm = Utils.getRealmInstance();
        order = realm.where(Order.class).equalTo("orderID", getIntent().getStringExtra("orderID")).findFirst();

        activityOrderDetailBinding.content.setOrder(order);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.deliveryPathMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        order_date.setText(Utils.getShorttenedDate(order.getDateCreated() * 1000));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng pickUpAddress = getLocationFromAddress(order.getPickupAddress());
        LatLng dropOffAddress = getLocationFromAddress(order.getDropOffAddress());

        if(pickUpAddress != null && dropOffAddress != null){
            googleMap.addMarker(new MarkerOptions()
                    .position(pickUpAddress)
                    .title(null)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            googleMap.addMarker(new MarkerOptions()
                    .position(dropOffAddress)
                    .title(null)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(pickUpAddress);
            builder.include(dropOffAddress);

            LatLngBounds bounds = builder.build();

            CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            googleMap.moveCamera(cameraUpdate);
            googleMap.animateCamera(cameraUpdate);
            googleMap.animateCamera(zoom);
        }
    }

    public LatLng getLocationFromAddress(String strAddress){
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng latLng = null;

        try{
            address = coder.getFromLocationName(strAddress, 5);

            if(address == null)
                return null;

            Address location = address.get(0);

            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

        return latLng;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @OnClick(R.id.back_button)
    public void backButton(){
        startActivity(new Intent(OrderDetailActivity.this, ProfileActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.contact_us)
    public void contactUsButton(){

    }

    @OnClick(R.id.rate_us)
    public void rateUsButton(){

    }

    @OnClick(R.id.feedback)
    public void feedbackButton(){

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderDetailActivity.this, ProfileActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
