package com.dmendel.domsweather.Weather;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WeatherUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void updateWeather() {
        List<City> cities = Cities.getInstance().getAllCities();
        assertNotNull(cities);
        // Expecting there to be the 3 hard coded cities
        assertFalse(cities.isEmpty());
        assertEquals(cities.size(), 3);

        Cities.CitiesListChangedListener citiesListChangedListener = new Cities.CitiesListChangedListener() {
            @Override
            public void cityAdded(City city) {

            }

            @Override
            public void cityRemoved(City city) {

            }
        };
    }

    @Test
    public void addNewCity() throws InterruptedException {
        List<City> cities = Cities.getInstance().getAllCities();
        assertNotNull(cities);
        // Expecting there to be the 3 hard coded cities
        assertFalse(cities.isEmpty());
        assertEquals(cities.size(), 3);

        Semaphore semaphore = new Semaphore(0);

        Cities.CitiesListChangedListener citiesListChangedListener = new Cities.CitiesListChangedListener() {
            @Override
            public void cityAdded(City city) {
                semaphore.release();
            }

            @Override
            public void cityRemoved(City city) {
                // Do nothing
            }
        };

        Cities.getInstance().addListener(citiesListChangedListener);
        WeatherUtils.addNewCity("Tampa");
        assertTrue(semaphore.tryAcquire(1, 5, TimeUnit.SECONDS));

        cities = Cities.getInstance().getAllCities();
        assertFalse(cities.isEmpty());
        assertEquals(cities.size(), 4);

        City tampa = Cities.getInstance().getCityByName("Tampa");
        assertNotNull(tampa);
        assertNotNull(tampa.getName());
        assertEquals(tampa.getName(), "Tampa");
        assertEquals(tampa.getLatitude(), 27.9477595, 0.1);
        assertEquals(tampa.getLongitude(), -82.458444, 0.1);

    }

}