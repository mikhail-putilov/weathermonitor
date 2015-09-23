package io.github.musius.config;

import com.google.common.collect.Lists;
import io.github.musius.service.util.WeatherCrawler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class WeatherServiceConfiguration {
    /**
     * Хак, из-за странного бага:
     * {@code @Value("${listOfStrings}") List<String> lst;} не обрабатывается корректно
     */
    @Bean
    public List<String> cities(@Value("${service.weather.cities[0]}") String city1, @Value("${service.weather.cities[1]}") String city2) {
        return Lists.newArrayList(city1, city2);
    }

    @Bean
    public List<WeatherCrawler> crawlers() {
        return Lists.newArrayList();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
