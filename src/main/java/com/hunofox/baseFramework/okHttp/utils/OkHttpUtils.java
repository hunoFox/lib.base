package com.hunofox.baseFramework.okHttp.utils;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.hunofox.baseFramework.okHttp.callback.BaseCallback;
import com.hunofox.baseFramework.okHttp.cookie.SimpleCookieJar;
import com.hunofox.baseFramework.okHttp.request.RequestCall;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;

public class OkHttpUtils {
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    private OkHttpUtils() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());
        mDelivery = new Handler(Looper.getMainLooper());
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        mOkHttpClient = okHttpClientBuilder.build();
    }


    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 异步执行
     */
    public void execute(final RequestCall requestCall, BaseCallback callback, final String flag) {

        if (callback == null)
            return;
        final BaseCallback finalCallback = callback;
        final Call call = requestCall.getCall();
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                sendFailResultCallback(call, -1, e, finalCallback, flag);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    try {
                        sendFailResultCallback(call, response.code(), new RuntimeException(response.body().string()), finalCallback, flag);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                try {
                    Object o = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, finalCallback, flag);
                } catch (Exception e) {
                    sendFailResultCallback(call, response.code(), e, finalCallback, flag);
                }
            }
        });
    }

    public void sendFailResultCallback(final Call call, final int responseCode, final Exception e, final BaseCallback callback, final String flag) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, responseCode, e, flag);
                callback.onAfter(flag);
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final BaseCallback callback, final String flag) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object, flag);
                callback.onAfter(flag);
            }
        });
    }

    public void setCertificates(X509TrustManager trustManager, InputStream... certificates) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null), trustManager)
                .build();
    }

    public void cancelTag(Object tag) {
        if (tag == null) {
            mOkHttpClient.dispatcher().cancelAll();
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag()) || tag == call.request().tag()) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag()) || tag == call.request().tag()) {
                call.cancel();
            }
        }
    }
}

