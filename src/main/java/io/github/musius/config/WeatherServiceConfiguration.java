package io.github.musius.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WeatherServiceConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
