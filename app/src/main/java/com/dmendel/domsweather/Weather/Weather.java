package com.dmendel.domsweather.Weather;

/**
 * The <code>Weather</code> is an object used to wrap weather related data for a city.
 */
public class Weather {

    private double currentTemperature;
    private double lowTemperature;
    private double highTemperature;
    private double feelsLikeTemperature;
    private double precipitation;
    private String weatherIconId;
    private String weatherDescription;

    protected Weather() {
        new Weather(0, 0, 0, 0, 0, "", "Unknown");
    }

    protected Weather(double currentTemperature, double lowTemperature, double highTemperature, double feelsLiketemperature, double precipitation, String weatherIconId, String weatherDescription) {
        this.currentTemperature = currentTemperature;
        this.lowTemperature = lowTemperature;
        this.highTemperature = highTemperature;
        this.feelsLikeTemperature = feelsLiketemperature;
        this.precipitation = precipitation;
        this.weatherIconId = weatherIconId;
        this.weatherDescription = weatherDescription;
    }


    public double getCurrentTemperature() {
        return currentTemperature;
    }

    protected void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public double getLowTemperature() {
        return lowTemperature;
    }

    protected void setLowTemperature(double lowTemperature) {
        this.lowTemperature = lowTemperature;
    }

    public double getHighTemperature() {
        return highTemperature;
    }

    protected void setHighTemperature(double highTemperature) {
        this.highTemperature = highTemperature;
    }

    public double getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }

    protected void setFeelsLikeTemperature(double feelsLikeTemperature) {
        this.feelsLikeTemperature = feelsLikeTemperature;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    protected void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public String getWeatherIconId() {
        return weatherIconId;
    }

    protected void setWeatherIconId(String weatherIconId) {
        this.weatherIconId = weatherIconId;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    protected void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }
}
