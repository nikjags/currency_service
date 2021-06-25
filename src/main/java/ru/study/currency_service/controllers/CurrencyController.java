package ru.study.currency_service.controllers;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.study.currency_service.services.interfaces.CurrencyService;
import ru.study.currency_service.services.interfaces.GifService;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.study.currency_service.services.interfaces.CurrencyService.PerformanceResult.UP;

@RestController
@RequestMapping("")
public class CurrencyController {
    private final Logger logger = getLogger(CurrencyController.class);
    @Autowired
    CurrencyService currencyServiceImpl;
    @Autowired
    GifService gifServiceImpl;
    @Value("${api.currency.baseCurrencyCode}")
    private String baseCurrencyCode;
    @Value("${api.gif.good-performance-search-word}")
    private String goodPerformanceSearchWord;
    @Value("${api.gif.bad-performance-search-word}")
    private String badPerformanceSearchWord;

    @GetMapping(value = "/currencyPerformance", produces = MediaType.IMAGE_GIF_VALUE)
    public byte[] getGifByCurrencyPerformance(@RequestParam(name = "currencyCode") String targetCurrencyCode) {
        validateCurrencyCode(targetCurrencyCode);

        String searchWord;

        var performance = currencyServiceImpl.getCurrencyPerformance(targetCurrencyCode);

        logger.info("Target currency: {}; base currency: {}; performance: {}", targetCurrencyCode, baseCurrencyCode, performance);

        if (performance == UP) {
            searchWord = "rich";
        } else {
            searchWord = "broke";
        }

        return gifServiceImpl.getRandomGifByTag(searchWord);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void validateCurrencyCode(String currencyCode) {
        if (!currencyCode.matches("^[A-Z]{3}$")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid currency code");
        }
    }
}
