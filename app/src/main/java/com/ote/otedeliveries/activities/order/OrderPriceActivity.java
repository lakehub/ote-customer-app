package com.ote.otedeliveries.activities.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.ote.otedeliveries.R;
import com.ote.otedeliveries.activities.account.ProfileActivity;
import com.ote.otedeliveries.activities.home.HomeActivity;
import com.ote.otedeliveries.activities.startup.LoginActivity;
import com.ote.otedeliveries.databinding.ActivityOrderPriceBinding;
import com.ote.otedeliveries.models.Order;
import com.ote.otedeliveries.models.User;
import com.ote.otedeliveries.rest.ApiClient;
import com.ote.otedeliveries.rest.ApiInterface;
import com.ote.otedeliveries.rest.response.Login;
import com.ote.otedeliveries.rest.response.PostResponse;
import com.ote.otedeliveries.sync.SyncUtils;
import com.ote.otedeliveries.utils.PrefManager;
import com.ote.otedeliveries.utils.Utils;

import java.util.Objects;
import java.util.UUID;

import br.com.zup.multistatelayout.MultiStateLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;

public class OrderPriceActivity extends AppCompatActivity {
    @BindView(R.id.complete_order_layout) MultiStateLayout complete_order_layout;

    Realm realm;
    Order order;
    User user;

    String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOrderPriceBinding activityOrderPriceBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_price);
        ButterKnife.bind(this);

        realm = Utils.getRealmInstance();
        order = realm.where(Order.class).equalTo("isBeingEdited", true).findFirst();

        activityOrderPriceBinding.content.setOrder(order);

        user = realm.where(User.class).findFirst();

        orderID = order.getOrderID();
    }

    @OnClick(R.id.cancel_button)
    public void cancelButton(){
        startActivity(new Intent(OrderPriceActivity.this, RequestDetailsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.back_button)
    public void backButton(){
        startActivity(new Intent(OrderPriceActivity.this, RequestDetailsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderPriceActivity.this, RequestDetailsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.finishOrder)
    public void makePayment(){
        realm.beginTransaction();
        order.setBeingEdited(false);
        order.setOrderCost("250");
        order.setInvoiceID(Utils.getInvoiceNumber());
        order.setUserID(user.getUserID());
        order.setOrderStatus(getString(R.string.pending));
        realm.insertOrUpdate(order);
        realm.commitTransaction();

        complete_order_layout.setState(MultiStateLayout.State.LOADING);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<PostResponse> deliveryOrderApiCall = apiInterface.deliveryOrder(
                user.getUserID(),
                order.getOrderID(),
                order.getPickupAddress(),
                order.getPickupContact(),
                order.getPickupContactNumber(),
                order.getDropOffAddress(),
                order.getDropOffContact(),
                order.getDropOffContactNumber(),
                order.getItemValueCost(),
                order.getNotes(),
                order.getOrderCost(),
                order.getInvoiceID(),
                order.getDateCreated()
        );

        deliveryOrderApiCall.enqueue(new Callback<PostResponse>(){
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull retrofit2.Response<PostResponse> response) {
                Order currentOrder = realm.where(Order.class).equalTo("orderID", orderID).findFirst();

                if(response.isSuccessful()){
                    if (response.body() != null && response.body().isSuccess()){
                        startActivity(new Intent(OrderPriceActivity.this, ProfileActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    else {
                        realm.beginTransaction();
                        if(currentOrder != null) {
                            currentOrder.setBeingEdited(true);
                            realm.insertOrUpdate(currentOrder);
                        }
                        realm.commitTransaction();

                        complete_order_layout.setState(MultiStateLayout.State.CONTENT);
                        Utils.getSimpleAlertDialog(OrderPriceActivity.this, (response.body() != null) ? response.body().getMessage() : getResources().getString(R.string.login_error)).show();
                    }
                }
                else {
                    realm.beginTransaction();
                    if(currentOrder != null) {
                        currentOrder.setBeingEdited(true);
                        realm.insertOrUpdate(currentOrder);
                    }
                    realm.commitTransaction();

                    complete_order_layout.setState(MultiStateLayout.State.CONTENT);
                    Utils.getSimpleAlertDialog(OrderPriceActivity.this, getResources().getString(R.string.login_error)).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t){
                Order currentOrder = realm.where(Order.class).equalTo("orderID", orderID).findFirst();

                realm.beginTransaction();
                if(currentOrder != null) {
                    currentOrder.setBeingEdited(true);
                    realm.insertOrUpdate(currentOrder);
                }
                realm.commitTransaction();

                complete_order_layout.setState(MultiStateLayout.State.CONTENT);
                Utils.getSimpleAlertDialog(OrderPriceActivity.this, getResources().getString(R.string.login_error)).show();
            }
        });
    }
}
