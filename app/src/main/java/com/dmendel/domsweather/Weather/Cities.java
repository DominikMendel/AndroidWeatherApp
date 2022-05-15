package com.dmendel.domsweather.Weather;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>Cities</code> maintains a list of all the known City objects and provides functionality
 * around that list.
 */
public class Cities {
    private static final String TAG = "Cities";

    public final String NEW_YORK = "New York, NY";
    public final String SAN_FRANCISCO = "San Francisco, CA";
    public final String SALT_LAKE = "Salt Lake City, UT";

    private final City NEW_YORK_CITY = new City(NEW_YORK, 40.7127281, -74.0060152);
    private final City SAN_FRANCISCO_CITY = new City(SAN_FRANCISCO, 37.7790262, -122.419906);
    private final City SALT_LAKE_CITY = new City(SALT_LAKE, 40.7596198, -111.8867975);

    private final List<CitiesListChangedListener> m_callBackList = new ArrayList<>();
    private List<City> m_allCities;

    private static Cities s_instance;

    public interface CitiesListChangedListener {
        void cityAdded(City city);
        void cityRemoved(City city);
    }

    private Cities() {
        m_allCities = new ArrayList<>();
        m_allCities.add(NEW_YORK_CITY);
        m_allCities.add(SAN_FRANCISCO_CITY);
        m_allCities.add(SALT_LAKE_CITY);
    }

    public static Cities getInstance() {
        if (s_instance == null) {
            s_instance = new Cities();
        }

        return s_instance;
    }

    public List<City> getAllCities() {
        return m_allCities;
    }

    public void addCity(City city) {
        // Make sure city doesn't currently exist in the list
        // Since there is no unique IDs associated with a city, use latitude + longitude
        for (City storedCity : m_allCities) {
            if ((city.getLatitude() == storedCity.getLatitude()) && (city.getLongitude() == storedCity.getLongitude())) {
                return;
            }
        }

        Log.d(TAG, "Adding city " + city);
        m_allCities.add(city);
        notifyCityAddedListeners(city);
    }

    public void removeCity(City city) {
        // Since there is no unique IDs associated with a city, use latitude + longitude
        for (City storedCity : m_allCities) {
            if ((city.getLatitude() == storedCity.getLatitude()) && (city.getLongitude() == storedCity.getLongitude())) {
                Log.d(TAG, "Removing city " + storedCity);
                m_allCities.remove(storedCity);
                notifyCityRemovedListeners(storedCity);
                return;
            }
        }
    }

    /**
     * @param cityName the city name to search by.
     * @return first city found by name. Be careful of duplicate city names.
     */
    public City getCityByName(String cityName) {
        for (City city : m_allCities) {
            if (city.getName().equals(cityName)) {
                return city;
            }
        }

        return null;
    }

    /**
     *
     * @param lat latitude of the city you want
     * @param lon longitude of the city you want
     * @return City object that matches input parameters
     */
    public City getCityByLatAndLon(int lat, int lon) {
        for (City city : m_allCities) {
            if (city.getLatitude() == lat && city.getLongitude() == lon) {
                return city;
            }
        }

        return null;
    }

    public void addListener(CitiesListChangedListener citiesListChangedCallback) {
        if (!m_callBackList.contains(citiesListChangedCallback)) {
            m_callBackList.add(citiesListChangedCallback);
        }
    }

    public void removeListener(CitiesListChangedListener citiesListChangedListener) {
        m_callBackList.remove(citiesListChangedListener);
    }

    private void notifyCityAddedListeners(City city) {
        Log.d(TAG, "Notifying listeners of adding city");
        Iterator<CitiesListChangedListener> iterator = m_callBackList.listIterator();
        while (iterator.hasNext()) {
            CitiesListChangedListener citiesListChangedListener = iterator.next();
            try {
                citiesListChangedListener.cityAdded(city);
            } catch (Exception e) {
                // Something went wrong notify a listener, so remove it.
                e.printStackTrace();
                removeListener(citiesListChangedListener);
            }
        }
    }

    private void notifyCityRemovedListeners(City city) {
        Log.d(TAG, "Notifying listeners of removing city");
        Iterator<CitiesListChangedListener> iterator = m_callBackList.listIterator();
        while (iterator.hasNext()) {
            CitiesListChangedListener citiesListChangedListener = iterator.next();
            try {
                citiesListChangedListener.cityRemoved(city);
            } catch (Exception e) {
                // Something went wrong notify a listener, so remove it.
                e.printStackTrace();
                removeListener(citiesListChangedListener);
            }
        }
    }
}
