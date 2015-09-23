package io.github.musius.web.rest;

import io.github.musius.domain.WeatherData;
import io.github.musius.repository.WeatherDataRepository;
import io.github.musius.web.rest.util.HeaderUtil;
import io.github.musius.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class WeatherDataResource {

    private final Logger log = LoggerFactory.getLogger(WeatherDataResource.class);

    @Inject
    private WeatherDataRepository weatherDataRepository;

    /**
     * POST  /weatherData -> Create a new weatherData.
     */
    @RequestMapping(value = "/weatherData",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WeatherData> createWeatherData(@Valid @RequestBody WeatherData weatherData) throws URISyntaxException {
        log.debug("REST request to save WeatherData : {}", weatherData);
        if (weatherData.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new weatherData cannot already have an ID").body(null);
        }
        WeatherData result = weatherDataRepository.save(weatherData);
        return ResponseEntity.created(new URI("/api/weatherData/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("weatherData", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /weatherData -> Updates an existing weatherData.
     */
    @RequestMapping(value = "/weatherData",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WeatherData> updateWeatherData(@Valid @RequestBody WeatherData weatherData) throws URISyntaxException {
        log.debug("REST request to update WeatherData : {}", weatherData);
        if (weatherData.getId() == null) {
            return createWeatherData(weatherData);
        }
        WeatherData result = weatherDataRepository.save(weatherData);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("weatherData", result.getId().toString()))
                .body(result);
    }

    /**
     * GET  /weatherData -> get all the weatherData.
     */
    @RequestMapping(value = "/weatherData",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WeatherData>> getAllWeatherData(Pageable pageable)
            throws URISyntaxException {
        Page<WeatherData> page = weatherDataRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/weatherData");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /weatherData/:id -> get the "id" weatherData.
     */
    @RequestMapping(value = "/weatherData/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WeatherData> getWeatherData(@PathVariable Long id) {
        log.debug("REST request to get WeatherData : {}", id);
        return Optional.ofNullable(weatherDataRepository.findOne(id))
                .map(weatherData -> new ResponseEntity<>(
                        weatherData,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /weatherData/:id -> delete the "id" weatherData.
     */
    @RequestMapping(value = "/weatherData/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteWeatherData(@PathVariable Long id) {
        log.debug("REST request to delete WeatherData : {}", id);
        weatherDataRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("weatherData", id.toString())).build();
    }
}
