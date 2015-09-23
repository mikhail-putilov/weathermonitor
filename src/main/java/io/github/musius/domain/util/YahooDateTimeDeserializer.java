package io.github.musius.domain.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class YahooDateTimeDeserializer extends JsonDeserializer<DateTime> {

    public static final String PATTERN = "EEE, d MMM yyyy h:mm a z";

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            try {
                Date date = new SimpleDateFormat(PATTERN).parse(str);
                return new DateTime(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new DateTime(jp.getLongValue());
        }
        throw ctxt.mappingException(handledType());
    }
}
