package com.whr.jhttp.helper;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by WuHaoran on 2016/12/16.
 */

public class WhrInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String TAG="WhrInterceptor";

    public interface Logger {
        void log(String message);

        //        /** A {@link HttpLoggingInterceptor.Logger} defaults output appropriate for the current platform. */
        WhrInterceptor.Logger DEFAULT = new WhrInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        };
    }

    public WhrInterceptor() {
        this(WhrInterceptor.Logger.DEFAULT);
    }

    public WhrInterceptor(WhrInterceptor.Logger logger) {
        this.logger = logger;
    }

    private final WhrInterceptor.Logger logger;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        StringBuilder requestStr = new StringBuilder();
        requestStr.append("请求地址：" + request.url());
//        String requestStartMessage = "网络请求地址--> "+request.url();
//        if (!logHeaders && hasRequestBody) {
//            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
//        }
//        logger.log(requestStartMessage);
        if (hasRequestBody) {
            if (requestBody.contentLength() != -1) {
//                logger.log("Content-Length: " + requestBody.contentLength());
                requestStr.append("。长度=" + requestBody.contentLength() + "。方法=" + request.method());
            }
        }
        logger.log(requestStr.toString());
        //请求体
        Buffer requestBuffer = new Buffer();
        requestBody.writeTo(requestBuffer);
        Charset reqCharset = UTF8;
        MediaType requestContentType = requestBody.contentType();
        if (requestContentType != null) {
            reqCharset = requestContentType.charset(UTF8);
        }
        logger.log("请求内容：" + requestBuffer.readString(reqCharset));
        //=================请求响应分割线==================
        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        //=================请求响应分割线==================
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();


        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        logger.log("响应码：" + response.code() + "。响应信息：" + response.message() + "。请求时间：" + tookMs + "ms" + "。响应体大小：" + buffer.size());

        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                logger.log("");
                logger.log("Couldn't decode the response body; charset is likely malformed.");
                logger.log("<-- END HTTP");
                return response;
            }
        }

        if (contentLength != 0) {
//            logger.log("响应结果信息：");
            logger.log("响应结果信息：" + buffer.clone().readString(charset));
        }

//        logger.log("响应结果大小：" + buffer.size()+"-->");
        return response;

    }

}
