package com.ote.otedeliveries.activities.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ote.otedeliveries.R;
import com.ote.otedeliveries.databinding.ActivityOrderQuotationBinding;
import com.ote.otedeliveries.models.Order;
import com.ote.otedeliveries.utils.Utils;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class OrderQuotationActivity extends AppCompatActivity implements OnMapReadyCallback {
    Realm realm;
    Order order;

    GoogleMap googleMap;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_order_quotation);
        ButterKnife.bind(this);

        realm = Utils.getRealmInstance();
        order = realm.where(Order.class).equalTo("isBeingEdited", true).findFirst();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.oteMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng pickUpAddress = getLocationFromAddress(order.getPickupAddress());
        LatLng dropOffAddress = getLocationFromAddress(order.getDropOffAddress());

        if(pickUpAddress != null && dropOffAddress != null){
            Marker pickupMarker = googleMap.addMarker(new MarkerOptions()
                    .position(pickUpAddress)
                    .title(order.getPickupAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            pickupMarker.showInfoWindow();

            Marker dropOffMarker = googleMap.addMarker(new MarkerOptions()
                    .position(dropOffAddress)
                    .title(order.getDropOffAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            dropOffMarker.showInfoWindow();

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

    @OnClick(R.id.confirm_order)
    public void confirmOrder(){
        startActivity(new Intent(OrderQuotationActivity.this, RequestDetailsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.back_button)
    public void backButton(){
        startActivity(new Intent(OrderQuotationActivity.this, SearchDestinationActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderQuotationActivity.this, SearchDestinationActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
