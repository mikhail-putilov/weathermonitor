package io.github.musius.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.musius.domain.WeatherData;
import io.github.musius.domain.util.YahooDateTimeDeserializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;

@Component
public class YahooWeatherCrawler implements WeatherCrawler {
    @Inject
    RestTemplate restTemplate;

    @Override
    public WeatherData getDataForCity(String cityName) {
        String json = restTemplate.getForObject(formatUri(cityName), String.class);
        return parseWeatherData(json);
    }

    private WeatherData parseWeatherData(String json) {
        ObjectMapper mapper = createObjectMapper();
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode part = root.at("/query/results/channel/item/condition");
            Fake fake = mapper.readValue(part.toString(), Fake.class);
            return fake.toWeatherData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectMapper createObjectMapper() {
        Jackson2ObjectMapperFactoryBean bean = new Jackson2ObjectMapperFactoryBean();
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    private String formatUri(String city) {
        return "https://query.yahooapis.com/v1/public/yql?q=" +
                "select item.condition from weather.forecast where woeid in " +
                "(select woeid from geo.places(1) where text='" + city + "')&format=json";
    }

    /**
     * Фейк, необходимый для правильного парсинга json от yahoo с дальнейим преобразованием в WeatherData
     */
    private static class Fake {
        public Double temp;
        @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
        @JsonDeserialize(using = YahooDateTimeDeserializer.class)
        public DateTime date;

        WeatherData toWeatherData() {
            WeatherData weatherData = new WeatherData();
            weatherData.setDate(date);
            weatherData.setTemperature(temp);
            return weatherData;
        }
    }
}
