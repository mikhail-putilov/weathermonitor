package io.github.musius.service.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import io.github.musius.domain.WeatherData;
import io.github.musius.domain.util.YahooDateTimeDeserializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class OpenweathermapWeatherCrawler implements WeatherCrawler {
    public static final String DATA_SOURCE_URI = "openweathermap.org";
    @Inject
    RestTemplate restTemplate;

    @Override
    public WeatherData getDataForCity(String cityName) {
        String json = restTemplate.getForObject(formatUri(cityName), String.class);
        ParseTemplate parsed = parse(json);
        return parsed.toWeatherData(cityName);
    }

    @Override
    public String getSupportedDataSourceUri() {
        return DATA_SOURCE_URI;
    }

    private ParseTemplate parse(String json) {
        ObjectMapper mapper = createObjectMapper();
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode part = root.at("/list").get(0);

            return mapper.readValue(part.toString(), ParseTemplate.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private ObjectMapper createObjectMapper() {
        Jackson2ObjectMapperFactoryBean bean = new Jackson2ObjectMapperFactoryBean();
        bean.setModules(Lists.newArrayList(new JodaModule()));
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    private String formatUri(String city) {
        return "http://api.openweathermap.org/data/2.5/find?q=" + city + "&units=metric";
    }

    /**
     * Шаблон-заглушка, необходимая для правильного парсинга json от yahoo с дальнейим преобразованием в WeatherData
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ParseTemplate {
        public HashMap<String, Number> main;
        public long dt; //unix timestamp

        WeatherData toWeatherData(String cityName) {
            WeatherData weatherData = new WeatherData();
            weatherData.setDate(new DateTime(dt * 1000L)); //timestamp -> joda time
            weatherData.setTemperatureCelsius(main.get("temp").doubleValue());
            weatherData.setCityName(cityName);
            weatherData.setDataSourceUri(DATA_SOURCE_URI);
            return weatherData;
        }
    }
}
