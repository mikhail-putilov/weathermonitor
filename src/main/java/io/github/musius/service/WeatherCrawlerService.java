package io.github.musius.service;

import com.google.common.collect.Lists;
import io.github.musius.domain.WeatherData;
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
    @Resource
    List<String> cities;
    @Inject
    WeatherDataRepository repo;

    public void populateDatabaseWithWeatherInformation() {
        List<WeatherData> toBeSaved = Lists.newArrayList();
        for (String city : cities) {
            for (WeatherCrawler crawler : crawlers) {
                WeatherData data = crawler.getDataForCity(city);
                toBeSaved.add(data);
            }
        }
        repo.save(toBeSaved);
    }

    @Scheduled(fixedDelayString = "${service.weather.fixed_delay_seconds}000")
    void autoPopulateDatabaseWithFixedDelay() {
        populateDatabaseWithWeatherInformation();
    }
}
