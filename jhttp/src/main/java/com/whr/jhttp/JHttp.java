package com.whr.jhttp;


import com.google.gson.Gson;
import com.whr.jhttp.helper.WhrInterceptor;
import com.whr.jhttp.request.IRequest;
import com.whr.jhttp.request.Url;
import com.whr.jhttp.response.ResponseCallback;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author 吴浩然
 *         封装okhttp3,主要提供postJson方法，以及对返回的Json进行解析
 *         上送的请求必须继承BaseRequest，响应必须继承BaseResponse
 */
public class JHttp {
    /**
     * 超时时间
     */
    private static final int TIME_OUT = 20;

    /**
     * 请求的主机名
     */
//	public static final String BASE_URL="http://192.168.1.140:8080/CRMShop/";
    private static final String BASE_URL="http://www.vvjoin.com:8080/CRMShop/";
    private static OkHttpClient mOkHttpClient;

    static {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpClientBuilder.addInterceptor(new WhrInterceptor());
        mOkHttpClient = okHttpClientBuilder.build();
    }

    /**
     * post和get的方法都是这个
     */
    private static void doHttp(Request request, ResponseCallback response) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(response);
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Json主要方法
     */
    public static void postJson(IRequest request, ResponseCallback response) {
        Request httpRequest = creatRequest(BASE_URL, request);
        doHttp(httpRequest, response);
    }

    private static Request creatRequest(String baseUrl, IRequest request) {
        //通过注解获取url
        Class<? extends IRequest> requestClass = request.getClass();
        boolean isAnnotationPresent = requestClass.isAnnotationPresent(Url.class);
        if (!isAnnotationPresent) {
            throw new RuntimeException("请在相关的Request上初始化接口值");
        }
        Url urlObj = requestClass.getAnnotation(Url.class);
        String urlStr = urlObj.value();
        //创建请求的Json
        String json = new Gson().toJson(request);
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        return new Request.Builder()
                .url(baseUrl + urlStr)
                .post(requestBody)
                .build();
    }

    public static void cancelAll() {
        mOkHttpClient.dispatcher().cancelAll();
    }

}