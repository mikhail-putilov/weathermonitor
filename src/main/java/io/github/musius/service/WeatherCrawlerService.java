package io.github.musius.service;

import io.github.musius.repository.WeatherDataRepository;
import io.github.musius.service.util.WeatherCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class WeatherCrawlerService {
    private final Logger log = LoggerFactory.getLogger(WeatherCrawlerService.class);

    @Resource
    List<WeatherCrawler> crawlers;
    @Inject
    WeatherDataRepository repo;

    public void populateDatabaseWithWeatherInformation() {
        /*for (AbstractWeatherClient client : crawlers) {
            for (Map.Entry<String, WeatherDto> entry : client.getWeatherDataForAllCities().entrySet()) {
                WeatherDto weatherDto = entry.getValue();
                WeatherData weatherData = new WeatherData();
                weatherData.setLastupDate(weatherDto.getDate());
                weatherData.setTemperature(weatherDto.getTemp());
                repo.save(weatherData);
            }
        }*/
    }

    @Scheduled(fixedDelayString = "${service.weather.fixed_delay_seconds}000")
    void autoPopulateDatabaseWithFixedDelay() {
        populateDatabaseWithWeatherInformation();
    }
}
