package ru.study.currency_service.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.jackson.JsonComponent;
import ru.study.currency_service.clients.pojos.GifObject;

import java.io.IOException;
import java.net.URI;

@JsonComponent
public class GifObjectDeserializerConfig {
    public static class GifObjectDeserializer extends JsonDeserializer<GifObject> {
        private GifObjectDeserializer() {
        }

        @Override
        public GifObject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            return new GifObject(
                URI.create(node
                    .get("data")
                    .get("images")
                    .get("original").get("url").asText()));
        }
    }

    private GifObjectDeserializerConfig() {
    }
}
