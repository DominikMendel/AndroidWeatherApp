package com.dmendel.domsweather.Weather;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>City</code> is an object used to wrap city related data and its weather information together.
 * A City must have a name, latitude, and longitude. Weather may be added later.
 */
public class City {

    private final String name;
    private final double latitude;
    private final double longitude;
    private final List<CityWeatherChangedListener> callBackList = new ArrayList<>();
    private Weather weather;

    public interface CityWeatherChangedListener {
        void weatherUpdated(City city);
    }

    public City(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
        notifyWeatherChangedListeners();
    }

    public void addListener(CityWeatherChangedListener cityWeatherChangedCallback) {
        if (!callBackList.contains(cityWeatherChangedCallback)) {
            callBackList.add(cityWeatherChangedCallback);
        }
    }

    public void removeListener(CityWeatherChangedListener cityWeatherChangedListener) {
        callBackList.remove(cityWeatherChangedListener);
    }

    private void notifyWeatherChangedListeners() {
        Iterator<CityWeatherChangedListener> iterator = callBackList.listIterator();
        while (iterator.hasNext()) {
            CityWeatherChangedListener cityWeatherChangedListener = iterator.next();
            try {
                cityWeatherChangedListener.weatherUpdated(this);
            } catch (Exception e) {
                // Something went wrong notifying a listener, so remove it.
                e.printStackTrace();
                removeListener(cityWeatherChangedListener);
            }
        }
    }
}
