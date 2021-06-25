package ru.study.currency_service.services.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.study.currency_service.clients.interfaces.GifClient;
import ru.study.currency_service.clients.pojos.GifObject;
import ru.study.currency_service.services.interfaces.GifService;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {GifServiceImpl.class})
class GifServiceImplTest {
    private static final String TAG = "tag string";
    private static final URI URI = java.net.URI.create("www.example.org");
    private static final byte[] GIF_BYTE_ARRAY = new byte[42];

    @MockBean
    private GifClient gifClient;

    @Autowired
    private GifService gifService;

    @Test
    void getRandomGifByTag_CallsGetRandomGifAndGetGifByUrl_ReturnsGifByteArray() {
        GifObject returnedGifObject = new GifObject();
        returnedGifObject.setOriginalGifUri(URI);

        when(gifClient.getRandomGifObjectByTag(anyString(), anyString())).thenReturn(returnedGifObject);
        when(gifClient.getGifByUrl(URI)).thenReturn(GIF_BYTE_ARRAY);

        assertEquals(GIF_BYTE_ARRAY, gifService.getRandomGifByTag(TAG));

        verify(gifClient, times(1)).getRandomGifObjectByTag(anyString(), eq(TAG));
        verify(gifClient, times(1)).getGifByUrl(URI);
    }
}