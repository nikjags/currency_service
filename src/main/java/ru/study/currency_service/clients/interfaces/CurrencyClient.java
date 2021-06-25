package ru.study.currency_service.clients.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.study.currency_service.clients.pojos.CurrencyResponse;

import java.time.LocalDate;

@FeignClient(value = "${api.currency.client-name}", url = "${api.currency.url}")
public interface CurrencyClient {

    /**
     * Gets latest currency values.
     * <p>
     * All prices are relative to USD.
     *
     * @param appId application ID needed for making requests.
     * @return {@code CurrencyResponse} containing currency values.
     */
    @GetMapping("/latest.json?app_id={appId}")
    CurrencyResponse getLatest(@PathVariable String appId);

    /**
     * Gets currency values for a specific date.
     * <p>
     * All prices are relative to USD.
     *
     * @param date  date to which currencies values are provided.
     * @param appId application ID needed for making requests;
     * @return {@code CurrencyResponse} containing currency values.
     */
    @GetMapping("/historical/{date}.json?app_id={appId}")
    CurrencyResponse getByDate(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @PathVariable String appId);
}
