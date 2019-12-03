package com.ote.otedeliveries.activities.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import com.ote.otedeliveries.R;
import com.ote.otedeliveries.activities.home.HomeActivity;
import com.ote.otedeliveries.adapters.PlacesAdapter;
import com.ote.otedeliveries.databinding.ActivitySearchDestinationBinding;
import com.ote.otedeliveries.models.Order;
import com.ote.otedeliveries.models.Place;
import com.ote.otedeliveries.utils.Utils;

import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class SearchDestinationActivity extends AppCompatActivity {
    @BindView(R.id.search_label) AppCompatTextView search_label;
    @BindView(R.id.txtPickup) AutoCompleteTextView txtPickup;
    @BindView(R.id.txtDropOff) AutoCompleteTextView txtDropOff;

    Realm realm;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySearchDestinationBinding activitySearchDestinationBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_destination);
        ButterKnife.bind(this);

        String currentAddress = getIntent().getStringExtra("clientAddress");

        txtPickup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    search_label.setText(getString(R.string.set_pickup));
            }
        });

        txtDropOff.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    search_label.setText(getString(R.string.set_dropoff));
            }
        });

        if(!TextUtils.isEmpty(currentAddress)){
            txtPickup.setText(currentAddress);
            txtDropOff.requestFocus();

            search_label.setText(getString(R.string.set_dropoff));
        }
        else{
            search_label.setText(getString(R.string.set_pickup));
            txtPickup.requestFocus();
        }

        realm = Utils.getRealmInstance();
        order = realm.where(Order.class).equalTo("isBeingEdited", true).findFirst();

        activitySearchDestinationBinding.content.setOrder(order);

        PlacesAdapter dropOffPlacesAdapter =  new PlacesAdapter(this);
        txtDropOff.setAdapter(dropOffPlacesAdapter);
        txtDropOff.setOnItemClickListener(dropOffOnItemClickListener);

        PlacesAdapter pickupPlacesAdapter =  new PlacesAdapter(this);
        txtPickup.setAdapter(pickupPlacesAdapter);
        txtPickup.setOnItemClickListener(pickupOnItemClickListener);
    }

    private AdapterView.OnItemClickListener dropOffOnItemClickListener = (adapterView, view, i, l) -> {
        String selectedPlace = ((Place) adapterView.getItemAtPosition(i)).getPlaceText();
        // Log.e("Selected Place", selectedPlace);
        // Toast.makeText(SearchDestinationActivity.this, selectedPlace, Toast.LENGTH_SHORT).show();

        txtDropOff.setText(selectedPlace);
    };

    private AdapterView.OnItemClickListener pickupOnItemClickListener = (adapterView, view, i, l) -> {
        String selectedPlace = ((Place) adapterView.getItemAtPosition(i)).getPlaceText();
        // Log.e("Selected Place", selectedPlace);
        // Toast.makeText(SearchDestinationActivity.this, selectedPlace, Toast.LENGTH_SHORT).show();

        txtPickup.setText(selectedPlace);
    };

    @OnClick(R.id.fare_button)
    public void fareButton(){
        if(TextUtils.isEmpty(txtPickup.getText())){
            txtPickup.setError(getString(R.string.input_missing));
            txtPickup.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(txtDropOff.getText())){
            txtDropOff.setError(getString(R.string.input_missing));
            txtDropOff.requestFocus();
            return;
        }

        if(order == null){
            order = new Order();
            order.setOrderID(UUID.randomUUID().toString());
        }

        realm.beginTransaction();
        order.setPickupAddress(Objects.requireNonNull(txtPickup.getText()).toString().trim());
        order.setDropOffAddress(Objects.requireNonNull(txtDropOff.getText()).toString().trim());
        order.setDateCreated(System.currentTimeMillis() / 1000L);
        order.setBeingEdited(true);
        realm.insertOrUpdate(order);
        realm.commitTransaction();

        startActivity(new Intent(SearchDestinationActivity.this, OrderQuotationActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.back_button)
    public void backButton(){
        startActivity(new Intent(SearchDestinationActivity.this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SearchDestinationActivity.this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}