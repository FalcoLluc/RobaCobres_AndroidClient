package com.example.robacobres_androidclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Servidor {
    //SERVICE USERS
    //register user
    @POST("users/register")
    Call<User> registerUser(@Body User user);

    //login user
    @POST("users/login")
    Call<Void> loginUser(@Body User user);

    //delete user
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String id);


    //SERVICE ITEMS
    //get all items
    @GET("items")
    Call<List<Item>> getItems();

    //get a specific item
    @GET("items/{id}")
    Call<Item> getItem(@Path("id") String id);



    /*
    //DELETE
    @DELETE("tracks/{id}")
    Call<Void> deleteTrack(@Path("id") String id);


    //PUT
    @PUT("tracks")
    Call<Void> putTrack(@Body Track track);

    //POST
    @POST("tracks")
    Call<Track> postTrack(@Body Track track);
     */
}
