package com.whr.jhttp.response.download;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by whrr5 on 2017/8/7.
 */

public class DownLoadHttp {
	/**
	 * 超时时间
	 */
	private static final int TIME_OUT = 15;

	/**
	 * 请求的主机名
	 */
//	public static final String BASE_URL="http://www.vvjoin.com:8090/file/appverfile/";
	private static OkHttpClient mOkHttpClient;

	static {
		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
		okHttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
		okHttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
		okHttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
		mOkHttpClient = okHttpClientBuilder.build();
	}

	public static void downLoad(String fileUrl, DownloadListener listener){
		downLoad(fileUrl,null,listener);
	}

	public static void downLoad(String fileUrl, String filePath, DownloadListener listener){
		final Request request = new Request.Builder().url(fileUrl).build();
		final Call call = mOkHttpClient.newCall(request);
		final DownloadCallback downloadCallback = DownloadCallback.getInstance();
		if(downloadCallback!=null){
			downloadCallback.init(listener,filePath);
		}else{
			return;
		}
		call.enqueue(downloadCallback);
	}
}