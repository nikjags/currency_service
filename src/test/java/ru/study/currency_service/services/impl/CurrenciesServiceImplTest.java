package ru.study.currency_service.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.study.currency_service.clients.interfaces.CurrencyClient;
import ru.study.currency_service.clients.pojos.CurrencyResponse;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static ru.study.currency_service.services.interfaces.CurrencyService.PerformanceResult.DOWN;
import static ru.study.currency_service.services.interfaces.CurrencyService.PerformanceResult.UP;

@SpringBootTest(classes = {CurrencyServiceImpl.class})
class CurrenciesServiceImplTest {
    private static final String EMPTY_STR = "";
    private static final String TOO_SHORT_STR = "aa";
    private static final String TOO_LONG_STR = "abcd";
    private static final String LOWERCASE_STR = "abc";
    private static final String STR_WITH_DIGITS = "2ab";
    private static final String STR_WITH_NON_LETTER_NON_DIGIT_SYMBOLS = "$ab";

    private static final String BASE_CURRENCY_FIELD_NAME = "baseCurrencyCode";
    private static final String BASE_CURRENCY_CODE = "ABC";

    private static final String TARGET_CURRENCY_CODE = "DEF";

    @MockBean
    private CurrencyClient currencyClient;

    @Autowired
    private CurrencyServiceImpl currencyService;

    @BeforeEach
    public void setUp() {
        setField(currencyService, BASE_CURRENCY_FIELD_NAME, BASE_CURRENCY_CODE);
    }

    @Test
    void getCurrencyPerformance_getsInvalidTargetCurrencyCode_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyPerformance(EMPTY_STR));
        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyPerformance(TOO_SHORT_STR));
        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyPerformance(TOO_LONG_STR));
        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyPerformance(LOWERCASE_STR));
        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyPerformance(STR_WITH_DIGITS));
        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyPerformance(STR_WITH_NON_LETTER_NON_DIGIT_SYMBOLS));
    }

    @Test
    void getCurrencyPerformance_GetsEqualBaseAndTargetCurrencyCodes_DoesNotCallCurrencyClient_ReturnsDownEnumValue() {
        assertEquals(DOWN, currencyService.getCurrencyPerformance(BASE_CURRENCY_CODE));

        verify(currencyClient, never()).getByDate(any(), any());
        verify(currencyClient, never()).getLatest(any());
    }

    @Test
    void getCurrencyPerformance_GetsBadCurrencyPerformance_ReturnsDownEnumValue() {
        Map<String, Double> yesterdayCurrencyPerformance = new HashMap<>();
        yesterdayCurrencyPerformance.put(BASE_CURRENCY_CODE, 1.5);
        yesterdayCurrencyPerformance.put(TARGET_CURRENCY_CODE, 1.3);
        CurrencyResponse yesterdayCurrencyResponse = new CurrencyResponse();
        yesterdayCurrencyResponse.setRates(yesterdayCurrencyPerformance);

        Map<String, Double> todayCurrencyPerformance = new HashMap<>();
        todayCurrencyPerformance.put(BASE_CURRENCY_CODE, 1.4);
        todayCurrencyPerformance.put(TARGET_CURRENCY_CODE, 1.5);
        CurrencyResponse todayCurrencyResponse = new CurrencyResponse();
        todayCurrencyResponse.setRates(todayCurrencyPerformance);

        when(currencyClient.getLatest(anyString()))
            .thenReturn(todayCurrencyResponse);
        when(currencyClient.getByDate(any(), anyString()))
            .thenReturn(yesterdayCurrencyResponse);

        assertEquals(DOWN, currencyService.getCurrencyPerformance(TARGET_CURRENCY_CODE));
    }

    @Test
    void getCurrencyPerformance_GetsGoodCurrencyPerformance_ReturnsUpEnumValue() {
        Map<String, Double> yesterdayCurrencyPerformance = new HashMap<>();
        yesterdayCurrencyPerformance.put(BASE_CURRENCY_CODE, 1.4);
        yesterdayCurrencyPerformance.put(TARGET_CURRENCY_CODE, 1.5);
        CurrencyResponse yesterdayCurrencyResponse = new CurrencyResponse();
        yesterdayCurrencyResponse.setRates(yesterdayCurrencyPerformance);

        Map<String, Double> todayCurrencyPerformance = new HashMap<>();
        todayCurrencyPerformance.put(BASE_CURRENCY_CODE, 1.5);
        todayCurrencyPerformance.put(TARGET_CURRENCY_CODE, 1.3);
        CurrencyResponse todayCurrencyResponse = new CurrencyResponse();
        todayCurrencyResponse.setRates(todayCurrencyPerformance);

        when(currencyClient.getLatest(anyString()))
            .thenReturn(todayCurrencyResponse);
        when(currencyClient.getByDate(any(), anyString()))
            .thenReturn(yesterdayCurrencyResponse);

        assertEquals(UP, currencyService.getCurrencyPerformance(TARGET_CURRENCY_CODE));
    }
}