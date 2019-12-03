package com.ote.otedeliveries.retrofit

import com.ote.otedeliveries.serializers.*
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    @GET("geocode/json")
    fun geoCodeAsync(@Query("address") address: String, @Query("key") apiKey: String,
                     @Query("region") region: String)
            : Deferred<Response<GeoCodingResponseSerializer>>

    @POST("user/auth/login")
    fun signInAsync(@Header("Authorization") auth: String): Deferred<Response<SignInSerializer>>

    @POST("user/")
    fun signUpAsync(@Body params: RequestBody): Deferred<Response<CommonPostResponseSerializer>>

    @GET("directions/json")
    fun getDirectionsAsync(@Query("origin") origin: String, @Query("destination") destination: String,
                           @Query("waypoints") waypoints: String, @Query("key") apiKey: String,
                           @Query("region") region: String)
            : Deferred<Response<DirectionsResponseSerializer>>

    @GET("snapToRoads")
    fun snapToRoadsAsync(@Query("path") path: String, @Query("key") apiKey: String,
                         @Query("interpolate") interpolate: Boolean)
            : Deferred<Response<SnapToRoadResponseSerializer>>

    @POST("user/fcm")
    fun sendDeviceIdAsync(@Header("Authorization") auth: String, @Body params: RequestBody)
            : Deferred<Response<CommonResponseSerializer>>

}