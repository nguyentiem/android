package com.example.rxretrofit.api;

import com.example.rxretrofit.constant.Constants;
import com.example.rxretrofit.data.Repos;
import com.example.rxretrofit.data.User;

import java.util.List;


import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitAPI {
    @GET("/users")
    Observable<List<User>> getUser();
//
//    @GET("/users/{userId}")
//    Observable<User> get();
//
    @GET("/users/{userId}/respo")
   Observable<User> getRepos(@Path("userId") String userID);
   public static RetrofitAPI getApi(){
       return new Retrofit.Builder()
                          .addConverterFactory(GsonConverterFactory.create())
                           .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                           .baseUrl(Constants.BASE_URL)
               .build()
               .create(RetrofitAPI.class);
   }
}
