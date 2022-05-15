package com.dmendel.domsweather;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dmendel.domsweather.Weather.Cities;
import com.dmendel.domsweather.Weather.City;
import com.dmendel.domsweather.Weather.Weather;
import com.dmendel.domsweather.Weather.WeatherUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private List<City> m_currentCityList = new ArrayList<>();
    private List<String> m_currentCityListNames = new ArrayList<>();
    private Map<String, City> m_cityNameToCity = new HashMap<>();
    private City m_currentSelectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = findViewById(R.id.citySpinner);
        spinner.setOnItemSelectedListener(m_onItemSelectedListener);

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(m_onRefreshWeatherClickListener);

        Button removeCityButton = findViewById(R.id.removeCityButton);
        removeCityButton.setOnClickListener(m_onRemoveCityButtonClickListener);

        Button addCityButton = findViewById(R.id.addCityInputButton);
        addCityButton.setOnClickListener(m_onNewCityInputButtonClickListener);
    }

    private final View.OnClickListener m_onRemoveCityButtonClickListener = v -> {
        Log.d(TAG, "Remove city button clicked");
        removeSelectedCity();
    };

    private final View.OnClickListener m_onNewCityInputButtonClickListener = v -> {
        Log.d(TAG, "Add new user input city button clicked");
        addNewInputCityToList();
    };

    private final View.OnClickListener m_onRefreshWeatherClickListener = v -> {
        Log.d(TAG, "Refresh weather button clicked");
        getWeatherUpdate();
    };

    private final City.CityWeatherChangedListener m_cityWeatherChangedListener = city -> runOnUiThread(() -> {
        Log.d(TAG, "weatherUpdated for city : " + city.getName());
        updateWeatherInformation(city);
    });

    private final Cities.CitiesListChangedListener m_citiesListChangedListener = new Cities.CitiesListChangedListener() {
        @Override
        public void cityAdded(City city) {
            runOnUiThread(() -> {
                Log.d(TAG, "City added " + city.getName());
                updateCitySpinner();
            });
        }

        @Override
        public void cityRemoved(City city) {
            runOnUiThread(() -> {
                Log.d(TAG, "City added " + city.getName());
                updateCitySpinner();
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Cities.getInstance().addListener(m_citiesListChangedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI elements which will get the initial weather for the first item in the list
        updateCitySpinner();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Cities.getInstance().removeListener(m_citiesListChangedListener);
    }

    private final AdapterView.OnItemSelectedListener m_onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedCityName = (String) parent.getItemAtPosition(position);
            City selectedCity = m_cityNameToCity.get(selectedCityName);
            if (selectedCity != null) {
                Log.d(TAG, "Selected city = " + selectedCity.getName());
                m_currentSelectedCity = selectedCity;
                getWeatherUpdate();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing
        }
    };

    private void updateCitySpinner() {
        m_currentCityList = new ArrayList<>();
        m_currentCityListNames = new ArrayList<>();
        m_cityNameToCity = new HashMap<>();

        // Get new list of cities
        // Then create mappings for the Spinner from the city names to the city objects
        m_currentCityList = Cities.getInstance().getAllCities();
        for (City city : m_currentCityList) {
            m_currentCityListNames.add(city.getName());
            m_cityNameToCity.put(city.getName(), city);
        }

        // Update spinner
        Spinner spinner = findViewById(R.id.citySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, m_currentCityListNames);
        spinner.setAdapter(adapter);

        if (!m_currentCityList.isEmpty()) {
            // Set index back to original city if there was one
            if (m_currentSelectedCity == null || !m_currentCityList.contains(m_currentSelectedCity)) {
                m_currentSelectedCity = m_currentCityList.get(0);
            }

            spinner.setSelection(m_currentCityList.indexOf(m_currentSelectedCity));
        } else {
            m_currentCityList = null;
            clearResults();
        }
    }

    private void getWeatherUpdate() {
        Spinner spinner = findViewById(R.id.citySpinner);
        String selectedCityName = (String) spinner.getSelectedItem();
        City selectedCity = m_cityNameToCity.get(selectedCityName);
        if (selectedCity != null) {
            selectedCity.addListener(m_cityWeatherChangedListener);
            WeatherUtils.updateWeather(selectedCity);
        }
    }

    private void clearResults() {
        TextView textView = findViewById(R.id.currentTempValue);
        textView.setText("");

        textView = findViewById(R.id.highTempValue);
        textView.setText("");

        textView = findViewById(R.id.lowTempValue);
        textView.setText("");

        textView = findViewById(R.id.feelsLikeTempValue);
        textView.setText("");

        textView = findViewById(R.id.precipitationValue);
        textView.setText("");

        textView = findViewById(R.id.weatherDescription);
        textView.setText("");
    }

    @SuppressLint("SetTextI18n")
    private void updateWeatherInformation(City city) {
        // Only update the information if the new city information is the selected city
        if (city == m_currentSelectedCity) {
            Weather cityWeather = city.getWeather();

            TextView textView = findViewById(R.id.currentTempValue);
            textView.setText(cityWeather.getCurrentTemperature() + " F");

            textView = findViewById(R.id.highTempValue);
            textView.setText(cityWeather.getHighTemperature() + " F");

            textView = findViewById(R.id.lowTempValue);
            textView.setText(cityWeather.getLowTemperature() + " F");

            textView = findViewById(R.id.feelsLikeTempValue);
            textView.setText(cityWeather.getFeelsLikeTemperature() + " F");

            textView = findViewById(R.id.precipitationValue);
            textView.setText(Double.toString(cityWeather.getPrecipitation()));

            textView = findViewById(R.id.weatherDescription);
            textView.setText(cityWeather.getWeatherDescription());

            updateWeatherIcon(cityWeather.getWeatherIconId());
        }
    }

    private void updateWeatherIcon(String iconId) {
        ImageView weatherIcon = findViewById(R.id.weatherIcon);
        WeatherUtils.updateWeatherIcon(getApplicationContext(), weatherIcon, iconId);
    }

    private void addNewInputCityToList() {
        EditText editText = findViewById(R.id.userInputEditText);
        Editable inputCity = editText.getText();
        Log.d(TAG, "Attempting to add city : " + inputCity.toString());
        WeatherUtils.addNewCity(inputCity.toString());
    }

    private void removeSelectedCity() {
        Spinner spinner = findViewById(R.id.citySpinner);
        String selectedCityName = (String) spinner.getSelectedItem();
        City selectedCity = m_cityNameToCity.get(selectedCityName);
        if (selectedCity != null) {
            Cities.getInstance().removeCity(selectedCity);
        }
    }
}