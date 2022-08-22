package com.example.requesttest;

import java.nio.file.attribute.UserPrincipal;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/users")
    Call<PageResponseBean> listUsers(@Query("page") Integer page);

    @POST("api/users")
    @FormUrlEncoded
    Call<UserBean> createUser(@Field("name") String name, @Field("job") String job);

    @GET("api/users")
    Observable<PageResponseBean> rxlistUsers(@Query("page") Integer page);

    @POST("api/users")
    @FormUrlEncoded
    Observable<UserBean> rxcreateUser(@Field("name") String name, @Field("job") String job);

    @POST("api/login")
    @FormUrlEncoded
    Observable<LoginBean> login(@Field("email") String email, @Field("password") String password);

    @GET("api/users/{userId}")
    Observable<UserDetailBean> rxUserInfo(@Path("userId") Integer userId);

    @GET("img/faces/{path}")
    Observable<ResponseBody> getAvatar(@Path("path") String path);
}
