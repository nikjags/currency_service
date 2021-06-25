package ru.study.currency_service.services.interfaces;


public interface GifService {
    /**
     * Gets and returns random gif related with tag value.
     *
     * @param tag word or phrase to which gif is related;
     * @return {@code byte[]} representation of the gif.
     */
    byte[] getRandomGifByTag(String tag);
}
