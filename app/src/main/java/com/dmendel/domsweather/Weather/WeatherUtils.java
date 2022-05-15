package com.dmendel.domsweather.Weather;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * The <code>WeatherUtils</code> provides various functionality to get and create new City
 * objects that contain weather.
 */
public class WeatherUtils {
    private static final String TAG = "WeatherImpl";

    // URLs for APIs
    private static final String WEATHER_URL = "/data/2.5/onecall?";
    private static final String GEO_COORDINATES_URL = "/geo/1.0/direct";
    private static final String ICON_FIRST_HALF_URL = "http://openweathermap.org/img/wn/";
    private static final String ICON_SECOND_HALF_URL = "@2x.png";
    // Keys for APIs
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";
    private static final String GEO_CITY_KEY = "q";
    private static final String LIMIT_KEY = "limit";
    private static final String UNITS_KEY = "units";
    private static final String FREEDOM_UNITS = "imperial";
    // Expected JSON elements
    private static final String CURRENT_JSON_ELEMENT = "current";
    private static final String TEMP_JSON_ELEMENT = "temp";
    private static final String FEELS_LIKE_JSON_ELEMENT = "feels_like";
    private static final String WEATHER_JSON_ELEMENT = "weather";
    private static final String DESCRIPTION_JSON_ELEMENT = "description";
    private static final String ICON_JSON_ELEMENT = "icon";
    private static final String MINUTELY_JSON_ELEMENT = "minutely";
    private static final String PRECIPITATION_JSON_ELEMENT = "precipitation";
    private static final String DAILY_JSON_ELEMENT = "daily";
    private static final String MIN_TEMP_JSON_ELEMENT = "min";
    private static final String MAX_TEMP_JSON_ELEMENT = "max";
    private static final String NAME_JSON_ELEMENT = "name";

    public static void updateWeather(City city) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(UNITS_KEY, FREEDOM_UNITS);
        requestParams.add(LATITUDE_KEY, Double.toString(city.getLatitude()));
        requestParams.add(LONGITUDE_KEY, Double.toString(city.getLongitude()));
        Log.d(TAG, "Getting weather for city " + city.getName() + "At lat " + city.getLatitude());
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            // @todo DJM this one is being used
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "onSuccess JSONObject.");
                updateCityInformation(city, response);
            }

            // @todo DJM this one is being used
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "Status code = " + statusCode + " Error response " + errorResponse);
            }
        };
        WeatherClient.get(WEATHER_URL, requestParams, jsonHttpResponseHandler);
    }

    public static void addNewCity(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            Log.d(TAG, "Invalid city name. Returning");
            return;
        }

        Log.d(TAG, "Attempting to add new city " + cityName);

        RequestParams requestParams = new RequestParams();
        requestParams.add(GEO_CITY_KEY, cityName);
        // @todo hard coded limit to 1 response, but should handle multiple responses if cities with same name are found.
        requestParams.add(LIMIT_KEY, "1");
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "onSuccess JSONArray.");
                addCityToList(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "Status code = " + statusCode + " Error response " + errorResponse);
            }
        };

        WeatherClient.get(GEO_COORDINATES_URL, requestParams, jsonHttpResponseHandler);
    }

    public static void updateWeatherIcon(Context context, ImageView imageView, String iconId) {
        String iconUrl = ICON_FIRST_HALF_URL + iconId + ICON_SECOND_HALF_URL;
        Log.d(TAG, "UpdatingWeatherIcon for " + iconId + " url = " +iconUrl);
        Glide.with(context).load(iconUrl).into(imageView);
    }

    // @todo Refactor this to use GSON. JSON is a mess to deal with.
    private static void updateCityInformation(City city, JSONObject jsonObject) {
        try {
            JSONObject currentJson = jsonObject.getJSONObject(CURRENT_JSON_ELEMENT);
            double currentTemperature = currentJson.getDouble(TEMP_JSON_ELEMENT);
            double feelsLikeTemperature = currentJson.getDouble(FEELS_LIKE_JSON_ELEMENT);

            JSONArray currentWeatherJsonArray = currentJson.getJSONArray(WEATHER_JSON_ELEMENT);
            JSONObject currentWeatherJson = currentWeatherJsonArray.getJSONObject(0);
            String weatherIconId = currentWeatherJson.getString(ICON_JSON_ELEMENT);
            String weatherDescription = currentWeatherJson.getString(DESCRIPTION_JSON_ELEMENT);

            JSONArray minutelyJsonArray = jsonObject.getJSONArray(MINUTELY_JSON_ELEMENT);
            JSONObject minutelyJson = minutelyJsonArray.getJSONObject(0);
            double precipitation = minutelyJson.getDouble(PRECIPITATION_JSON_ELEMENT);

            JSONArray dailyJsonArray = jsonObject.getJSONArray(DAILY_JSON_ELEMENT);
            JSONObject dailyJson = dailyJsonArray.getJSONObject(0);
            JSONObject dailyTemperatureJson = dailyJson.getJSONObject(TEMP_JSON_ELEMENT);
            double lowTemperature = dailyTemperatureJson.getDouble(MIN_TEMP_JSON_ELEMENT);
            double highTemperature = dailyTemperatureJson.getDouble(MAX_TEMP_JSON_ELEMENT);

            Weather updatedWeather = new Weather(currentTemperature, lowTemperature, highTemperature, feelsLikeTemperature, precipitation, weatherIconId, weatherDescription);
            city.setWeather(updatedWeather);
        } catch (JSONException e) {
            // Choosing to not alter the current weather object if something happens.
            // Another option it to reset it, or put some 'unknown' value in.
            e.printStackTrace();
        }
    }

    private static void addCityToList(JSONArray jsonArray) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String cityName = jsonObject.getString(NAME_JSON_ELEMENT);
            double latitude = jsonObject.getDouble(LATITUDE_KEY);
            double longitude = jsonObject.getDouble(LONGITUDE_KEY);

            Log.d(TAG, "City form JSON API response. Name = " + cityName + " latitude = " + latitude + " longitude = " + longitude);
            City city = new City(cityName, latitude, longitude);
            Cities.getInstance().addCity(city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
