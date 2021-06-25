package ru.study.currency_service.clients.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.study.currency_service.clients.pojos.GifObject;

import java.net.URI;

import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;

@FeignClient(name = "${api.gif.client-name}", url = "${api.gif.url}")
public interface GifClient {

    /**
     * Gets a random GIF Object relative to the {@code tagWord}. The object contains a variety of information, such as the Image Object,
     * which itself includes the URLS for multiple different GIFS formats and sizes.
     *
     * @param appKey  application key is used for making requests;
     * @param tagWord word or phrase to which the gif is related.
     * @return JsonNode representation of the GIF object.
     */
    @GetMapping("/random?api_key={appKey}&tag={tagWord}")
    GifObject getRandomGifObjectByTag(@PathVariable String appKey, @PathVariable String tagWord);

    /**
     * Gets a gif from provided URI.
     *
     * @param host gift URI
     * @return {@code Response} instance with the gif in the body.
     */
    @GetMapping(produces = IMAGE_GIF_VALUE)
    //@Headers("Accept: image/gif")
    byte[] getGifByUrl(URI host);
}