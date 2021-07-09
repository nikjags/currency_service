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

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.study.currency_service.services.interfaces.CurrencyService.PerformanceResult.UP;

@RestController
@RequestMapping("")
public class CurrencyController {
    private static final Pattern CURRENCY_PATTERN = compile("^[A-Z]{3}$");

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

        var performanceResult = currencyServiceImpl.getCurrencyPerformance(targetCurrencyCode);

        logger.info("Target currency: {}; base currency: {}; performanceResult: {}", targetCurrencyCode, baseCurrencyCode, performanceResult);

        String searchWord = performanceResult == UP ? goodPerformanceSearchWord : badPerformanceSearchWord;

        return gifServiceImpl.getRandomGifByTag(searchWord);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void validateCurrencyCode(String currencyCode) {
        if (!CURRENCY_PATTERN.matcher(currencyCode).matches()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid currency code");
        }
    }
}
