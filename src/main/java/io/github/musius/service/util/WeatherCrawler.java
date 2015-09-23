package io.github.musius.service.util;

import io.github.musius.domain.WeatherData;

public interface WeatherCrawler {
    WeatherData getDataForCity(String cityName);
}
