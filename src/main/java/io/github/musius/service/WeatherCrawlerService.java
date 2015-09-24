package io.github.musius.service;

import com.google.common.collect.Lists;
import io.github.musius.domain.WeatherData;
import io.github.musius.repository.WeatherDataRepository;
import io.github.musius.service.util.WeatherCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "service.weather")
@Service
public class WeatherCrawlerService {
    private final Logger log = LoggerFactory.getLogger(WeatherCrawlerService.class);

    @Autowired
    List<WeatherCrawler> crawlers;

    private List<String> cities = new ArrayList<>(); //from properties

    @Inject
    WeatherDataRepository repo;

    public void populateDatabaseWithNewWeatherInformation() {
        List<WeatherData> toBeSaved = Lists.newArrayList();
        for (String city : getCities()) {
            for (WeatherCrawler crawler : crawlers) {
                tryCrawlDataOrSilence(toBeSaved, city, crawler);
            }
        }
        repo.save(toBeSaved);
    }

    private void tryCrawlDataOrSilence(List<WeatherData> toBeSaved, String city, WeatherCrawler crawler) {
        try {
            WeatherData data = crawler.getDataForCity(city);
            toBeSaved.add(data);
        } catch (Exception e) {
            log.error("Failed to crawl data from {} for city '{}'", crawler.getSupportedDataSourceUri(), city);
        }
    }

    @Scheduled(fixedDelayString = "${service.weather.fixed_delay_seconds}000")
    void autoPopulateDatabaseWithFixedDelay() {
        populateDatabaseWithNewWeatherInformation();
    }

    public List<String> getCities() {
        return cities;
    }
}
