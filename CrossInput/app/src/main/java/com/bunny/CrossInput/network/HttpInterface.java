package com.bunny.CrossInput.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Project:  TSPL蓝牙打印机
 * Comments: 客户端Retrofit接口类
 * JDK version used: <JDK1.8>
 * Create Date：2023-02-25
 * Version: 1.0
 */

public interface HttpInterface {
    //连接测试注解
    @GET("/")
    Call<ResponseBody> testLink();
    //删除文本注解
    @GET("/del")
    Call<ResponseBody> delText();
    //光标上移注解
    @GET("/up")
    Call<ResponseBody> up();
    //光标下移注解
    @GET("/down")
    Call<ResponseBody> down();
    //光标左移注解
    @GET("/left")
    Call<ResponseBody> left();
    //光标右移注解
    @GET("/right")
    Call<ResponseBody> right();
    @GET("/enter")
    Call<ResponseBody> enter();
    //发送文本到服务器
    @POST
    Call<ResponseBody> addText(@Url String url);
}
