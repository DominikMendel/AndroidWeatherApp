package com.dmendel.domsweather.Weather;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * The <code>WeatherClient</code> is used to get/post the openweathermpa.org's API.
 */
public class WeatherClient {
    private static final String TAG = "WeatherClient";

    private static final String API_KEY = "FILL WITH YOUR API KEY";
    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final AsyncHttpClient m_client = new AsyncHttpClient();

    protected static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("appid", API_KEY);
        m_client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    protected static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("appid", API_KEY);
        m_client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d(TAG, "getAbsoluteUrl = " + BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }

}
