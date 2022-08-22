package com.example.requesttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new LoggingInterceptor())
            .build();
    private static final String TAG = "haohaotest";
    private TextView tvContent;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test).setOnClickListener(this);
        tvContent = findViewById(R.id.tv_content);
        imageView = findViewById(R.id.iv_avatar);
    }

    @Override
    public void onClick(View view) {
        finalDemo();
    }

    void okhttpDemo(){
        Request request = new Request.Builder().url("https://reqres.in/api/users?page=2").build();
        tvContent.setText("请求中...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Response response = client.newCall(request).execute();
                    Log.d(TAG, "okhttpDemo: " + response.body().string());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvContent.setText("请求返回的状态码:" + response.code());
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void okhttpAysncDemo(){
        Request request = new Request.Builder().url("https://reqres.in/api/users?page=2").build();
        tvContent.setText("请求中...");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: 请求失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.setText("请求失败");
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "okhttpDemo: " + response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.setText("请求返回的状态码:" + response.code());
                    }
                });
            }
        });
    }

    void okhttpParams(){
        HttpUrl.Builder builder = HttpUrl.parse("https://reqres.in/api/users").newBuilder();
        builder.addQueryParameter("page", "2");
        String url = builder.build().toString();
        Log.d(TAG, "okhttpParams: " + url);
    }

    void okhttpPostDemo(){
        tvContent.setText("请求中...");
        RequestBody body = new FormBody.Builder()
                .add("name", "haohao")
                .add("job", "developer")
                .build();
        Request request = new Request.Builder().url("https://reqres.in/api/users")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "okhttpDemo: " + response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.setText("请求返回的状态码:" + response.code());
                    }
                });
            }
        });
    }

    void retrofitGetDemo(){
        tvContent.setText("请求中...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(ApiService.class).listUsers(2).enqueue(new retrofit2.Callback<PageResponseBean>() {
            @Override
            public void onResponse(retrofit2.Call<PageResponseBean> call, retrofit2.Response<PageResponseBean> response) {
                Log.d(TAG, "okhttpDemo: " + response.body().page);
                tvContent.setText("请求返回的状态码:" + response.code());
            }

            @Override
            public void onFailure(retrofit2.Call<PageResponseBean> call, Throwable t) {

            }
        });
    }

    void retrofitPostDemo()    {
        tvContent.setText("请求中...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(ApiService.class).createUser("haohao", "developer").enqueue(new retrofit2.Callback<UserBean>() {
            @Override
            public void onResponse(retrofit2.Call<UserBean> call, retrofit2.Response<UserBean> response) {
                Log.d(TAG, "okhttpDemo: name: " + response.body().name + " job: " + response.body().job + " id: " + response.body().id);
                tvContent.setText("请求返回的状态码:" + response.code());
            }

            @Override
            public void onFailure(retrofit2.Call<UserBean> call, Throwable t) {

            }
        });
    }

    void rxJavaGetDemo()    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class).rxlistUsers(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PageResponseBean>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        tvContent.setText("请求中...");
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull PageResponseBean pageResponseBean) {
                        Log.d(TAG, "onNext: " + pageResponseBean.totalPages);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        tvContent.setText("请求完成");
                    }
                });
    }

    void rxJavaPostDemo()    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class).rxcreateUser("haohao", "developer")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserBean>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        tvContent.setText("请求中...");
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull UserBean userBean) {
                        Log.d(TAG, "okhttpDemo: name: " + userBean.name + " job: " + userBean.job + " id: " + userBean.id);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        tvContent.setText("请求完成");
                    }
                });
    }


    /**
     * 1. 获取用户列表 https://reqres.in/api/users?page=2
     * 2. 显示用户列表用户总个数 total
     * 3. 创建新用户 post https://reqres.in/api/users
     * 4. 显示创建的用户的名字 name
     * 5. 登录 POST: https://reqres.in/api/login
     * 6. 显示登录的token token
     * 7. 获取单个用户信息 get: https://reqres.in/api/users/2
     * 8. 显示用户的头像 avatar
     */
    void finalDemo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class).rxlistUsers(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<PageResponseBean>() {
                    @Override
                    public void accept(PageResponseBean pageResponseBean) throws Throwable {
                        Log.d(TAG, "总人数为: " + pageResponseBean.total);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<PageResponseBean, ObservableSource<UserBean>>() {
                    @Override
                    public ObservableSource<UserBean> apply(PageResponseBean pageResponseBean) throws Throwable {
                        return retrofit.create(ApiService.class).rxcreateUser("haohao", "developer");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<UserBean>() {
                    @Override
                    public void accept(UserBean userBean) throws Throwable {
                        tvContent.setText("当前用户名字是: " + userBean.name);
                    }
                }) // 显示当前用户名
                .observeOn(Schedulers.io())
                .flatMap(new Function<UserBean, ObservableSource<LoginBean>>() {
                    @Override
                    public ObservableSource<LoginBean> apply(UserBean userBean) throws Throwable {
                        return retrofit.create(ApiService.class).login("eve.holt@reqres.in", "cityslicka");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<LoginBean>() {
                    @Override
                    public void accept(LoginBean loginBean) throws Throwable {
                        tvContent.setText("登录成功token: " + loginBean.token);
                    }
                }) // 显示登录token
                .observeOn(Schedulers.io())
                .flatMap(new Function<LoginBean, ObservableSource<UserDetailBean>>() {
                    @Override
                    public ObservableSource<UserDetailBean> apply(LoginBean loginBean) throws Throwable {
                        return retrofit.create(ApiService.class).rxUserInfo(2);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<UserDetailBean, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(UserDetailBean userDetailBean) throws Throwable {
                        return Observable.just(userDetailBean.data.avatar);
                    }
                }) // 返回用户头像地址
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(String s) throws Throwable {
                        String[] paths = s.split("/");
                        String path = paths[paths.length - 1];
                        return retrofit.create(ApiService.class).getAvatar(path);
                    }
                })
                .map(new Function<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap apply(ResponseBody responseBody) throws Throwable {
                        return BitmapFactory.decodeStream(responseBody.byteStream());
                    }
                }) // 获取用户头像转化成为bitmap
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}