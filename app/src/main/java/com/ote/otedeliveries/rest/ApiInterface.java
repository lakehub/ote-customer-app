package com.ote.otedeliveries.rest;

import com.ote.otedeliveries.rest.response.Login;
import com.ote.otedeliveries.rest.response.PostResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login/client")
    Call<Login> loginUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/push/notification")
    Call<PostResponse> pushNotification(@Field("user_id") String userID, @Field("push_id") String pushID);

    @FormUrlEncoded
    @POST("delivery/order/add")
    Call<PostResponse> deliveryOrder(
            @Field("user_id") String userID,
            @Field("order_id") String orderID,
            @Field("pickup_address") String pickupAddress,
            @Field("pickup_contact") String pickupContact,
            @Field("pickup_contact_number") String pickupContactNumber,
            @Field("dropoff_address") String dropoffAddress,
            @Field("dropoff_contact") String dropoffContact,
            @Field("dropoff_contact_number") String dropoffContactNumber,
            @Field("item_value_cost") String itemValueCost,
            @Field("notes") String notes,
            @Field("delivery_fee") String deliveryFee,
            @Field("invoice_id") String invoiceID,
            @Field("date_created") long dateCreated
    );

    @FormUrlEncoded
    @POST("user/add")
    Call<PostResponse> addUser(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email,
            @Field("phone_number") String phoneNumber,
            @Field("password") String password,
            @Field("role") int role
    );
 }