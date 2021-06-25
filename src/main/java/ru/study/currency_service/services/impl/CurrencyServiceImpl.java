package ru.study.currency_service.services.impl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.study.currency_service.clients.interfaces.CurrencyClient;
import ru.study.currency_service.clients.pojos.CurrencyResponse;
import ru.study.currency_service.services.interfaces.CurrencyService;

import static java.time.LocalDate.now;
import static java.time.ZoneId.of;
import static java.time.temporal.ChronoUnit.DAYS;
import static ru.study.currency_service.services.interfaces.CurrencyService.PerformanceResult.*;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Value("${api.currency.appId}")
    private String appId;

    @Value("${api.currency.zoneId}")
    private String zoneIdStr;

    @Value("${api.currency.url}")
    private String currencyServiceUrl;

    @Value("${api.currency.baseCurrencyCode}")
    private String baseCurrencyCode;

    @Autowired
    private CurrencyClient currencyClient;

    public PerformanceResult getCurrencyPerformance(@NonNull String targetCurrencyCode) {
        if (!targetCurrencyCode.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("CurrencyCode must be valid");
        }

        if (targetCurrencyCode.equals(baseCurrencyCode)) {
            return DOWN;
        }

        CurrencyResponse todayPerformance = currencyClient.getLatest(appId);

        var todayTCurrencyValue = todayPerformance.getRates().get(targetCurrencyCode);
        var todayBCurrencyValue = todayPerformance.getRates().get(baseCurrencyCode);

        CurrencyResponse yesterdayPerformance = currencyClient.getByDate(
            now(of(zoneIdStr)).minus(1, DAYS),
            appId);

        var yesterdayTCurrencyValue = yesterdayPerformance.getRates().get(targetCurrencyCode);
        var yesterdayBCurrencyValue = yesterdayPerformance.getRates().get(baseCurrencyCode);

        return isCurrencyMakingGoodToday(todayTCurrencyValue, todayBCurrencyValue, yesterdayTCurrencyValue, yesterdayBCurrencyValue) ? UP : DOWN;
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    /**
     * <p>Compares yesterday and today performance of the target currency relative to the base one.</p>
     *
     * <p>For now Open Exchange Rates API returns all currency values relative to the USD (in free plan),</p>
     * <p>but it's okay cause we can just divide target-to-usd currency value
     * into base-to-usd currency value and get target-to-base value.</p>
     *
     * @param todayTargetCurrencyValue     today's target-to-usd currency value;
     * @param todayBaseCurrencyValue       today's base-to-usd currency value;
     * @param yesterdayTargetCurrencyValue yesterday's target-to-usd currency value;
     * @param yesterdayBaseCurrencyValue   yesterday's base-to-usd currency value.
     * @return true if today target currency gets stronger relative to base currency.
     */
    private static boolean isCurrencyMakingGoodToday(
        double todayTargetCurrencyValue, double todayBaseCurrencyValue,
        double yesterdayTargetCurrencyValue, double yesterdayBaseCurrencyValue) {

        return (todayTargetCurrencyValue / todayBaseCurrencyValue) < (yesterdayTargetCurrencyValue / yesterdayBaseCurrencyValue);
    }
}
