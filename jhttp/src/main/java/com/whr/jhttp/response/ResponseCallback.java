package com.whr.jhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author vision
 * 专门处理JSON的回调
 * 基于返回值的response的code和msg一定
 */
public abstract class ResponseCallback<T extends BaseResponse>implements Callback {


	/**
	 * 将其它线程的数据转发到UI线程
	 */
	private Handler mDeliveryHandler;

	public ResponseCallback() {
		this.mDeliveryHandler = new Handler(Looper.getMainLooper());
	}

	/**
	 * 一般就是网络连接错误时候
	 * @param call
	 * @param ioexception
	 */
	@Override
	public void onFailure(final Call call, final IOException ioexception) {
		mDeliveryHandler.post(new Runnable() {
			@Override
			public void run() {
				onFail("网络连接错误");
			}
		});
	}

	@Override
	public void onResponse(final Call call, final Response response) throws IOException {
		final String result = response.body().string();
		ParameterizedType genericType = (ParameterizedType) this.getClass()
				.getGenericSuperclass();
		Type[] types = genericType.getActualTypeArguments();
		Type type  = types[0];
		Gson gson=new Gson();
		try{
			final T resultBean= gson.fromJson(result, type);
			mDeliveryHandler.post(new Runnable() {
				@Override
				public void run() {
					onSucess(resultBean);
				}
			});
		}catch (JsonSyntaxException e){
			//如这种直接返回文字的
			//<html><head><title>Apache Tomcat/7.0.42 - Error report</title><style><!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> </head><body><h1>HTTP Status 404 - There is no Action mapped for namespace [/] and action name [sendUnionOrderList] associated with context path [/CRMShop].</h1><HR size="1" noshade="noshade"><p><b>type</b> Status report</p><p><b>message</b> <u>There is no Action mapped for namespace [/] and action name [sendUnionOrderList] associated with context path [/CRMShop].</u></p><p><b>description</b> <u>The requested resource is not available.</u></p><HR size="1" noshade="noshade"><h3>Apache Tomcat/7.0.42</h3></body></html>
			mDeliveryHandler.post(new Runnable() {
				@Override
				public void run() {
					onFail("服务器信息：500");
				}
			});
		}
	}

	public abstract void onSucess(T t);

	public abstract void onFail(String msg);
}