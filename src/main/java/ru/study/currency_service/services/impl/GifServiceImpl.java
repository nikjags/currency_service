package ru.study.currency_service.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.study.currency_service.clients.interfaces.GifClient;
import ru.study.currency_service.services.interfaces.GifService;

@Service
public class GifServiceImpl implements GifService {
    @Value("${api.gif.appId}")
    private String appId;

    @Value("${api.gif.url}")
    private String apiUrl;

    @Autowired
    private GifClient gifClient;

    public byte[] getRandomGifByTag(String tag) {
        var gifObject = gifClient.getRandomGifObjectByTag(appId, tag);

        return gifClient.getGifByUrl(gifObject.getOriginalGifUri());
    }
}
