package io.github.musius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherMonitorApplication.class, args);
    }
}
