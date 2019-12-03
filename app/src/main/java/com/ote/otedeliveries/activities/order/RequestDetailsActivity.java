package com.ote.otedeliveries.activities.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ote.otedeliveries.R;
import com.ote.otedeliveries.activities.home.HomeActivity;
import com.ote.otedeliveries.databinding.ActivityRequestDetailsBinding;
import com.ote.otedeliveries.models.Order;
import com.ote.otedeliveries.utils.Utils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class RequestDetailsActivity extends AppCompatActivity {
    @BindView(R.id.pickup_address_input) TextInputLayout pickup_address_input;
    @BindView(R.id.pickup_address_text) TextInputEditText pickup_address_text;

    @BindView(R.id.pickup_contact_name_input) TextInputLayout pickup_contact_name_input;
    @BindView(R.id.pickup_contact_name_text) TextInputEditText pickup_contact_name_text;

    @BindView(R.id.pickup_contact_number_input) TextInputLayout pickup_contact_number_input;
    @BindView(R.id.pickup_contact_number_text) TextInputEditText pickup_contact_number_text;

    @BindView(R.id.dropoff_address_input) TextInputLayout dropoff_address_input;
    @BindView(R.id.drop_off_address_text) TextInputEditText drop_off_address_text;

    @BindView(R.id.dropoff_contact_name_input) TextInputLayout dropoff_contact_name_input;
    @BindView(R.id.drop_off_contact_name_text) TextInputEditText drop_off_contact_name_text;

    @BindView(R.id.dropoff_contact_number_input) TextInputLayout dropoff_contact_number_input;
    @BindView(R.id.drop_off_contact_number_text) TextInputEditText drop_off_contact_number_text;

    @BindView(R.id.notes_input) TextInputLayout notes_input;
    @BindView(R.id.notes_text) TextInputEditText notes_text;

    Realm realm;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRequestDetailsBinding activityRequestDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_request_details);
        ButterKnife.bind(this);

        realm = Utils.getRealmInstance();
        order = realm.where(Order.class).equalTo("isBeingEdited", true).findFirst();

        activityRequestDetailsBinding.content.setOrder(order);
    }

    @OnClick(R.id.back_button)
    public void backButton(){
        startActivity(new Intent(RequestDetailsActivity.this, SearchDestinationActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.cancel_button)
    public void cancelButton(){
        startActivity(new Intent(RequestDetailsActivity.this, SearchDestinationActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.request_rider)
    public void fareButton(){
        if(TextUtils.isEmpty(pickup_address_text.getText())){
            pickup_address_input.setError(getString(R.string.input_missing));
            pickup_address_text.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(pickup_contact_name_text.getText())){
            pickup_contact_name_input.setError(getString(R.string.input_missing));
            pickup_contact_name_text.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(pickup_contact_number_text.getText())){
            pickup_contact_number_input.setError(getString(R.string.input_missing));
            pickup_contact_number_text.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(drop_off_address_text.getText())){
            dropoff_address_input.setError(getString(R.string.input_missing));
            drop_off_address_text.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(drop_off_contact_name_text.getText())){
            dropoff_contact_name_input.setError(getString(R.string.input_missing));
            drop_off_contact_name_text.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(drop_off_contact_number_text.getText())){
            dropoff_contact_number_input.setError(getString(R.string.input_missing));
            drop_off_contact_number_text.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(notes_text.getText())){
            notes_input.setError(getString(R.string.input_missing));
            notes_text.requestFocus();
            return;
        }

        realm.beginTransaction();
        order.setPickupContact(Objects.requireNonNull(pickup_contact_name_text.getText()).toString().trim());
        order.setPickupContactNumber(Objects.requireNonNull(pickup_contact_number_text.getText()).toString().trim());
        order.setDropOffContact(Objects.requireNonNull(drop_off_contact_name_text.getText()).toString().trim());
        order.setDropOffContactNumber(Objects.requireNonNull(drop_off_contact_number_text.getText()).toString().trim());
        order.setItemValueCost("0");
        order.setNotes(Objects.requireNonNull(notes_text.getText()).toString().trim());
        realm.insertOrUpdate(order);
        realm.commitTransaction();

        startActivity(new Intent(RequestDetailsActivity.this, OrderPriceActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RequestDetailsActivity.this, SearchDestinationActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
