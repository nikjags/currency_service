package ru.study.currency_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.study.currency_service.clients.interfaces.CurrencyClient;
import ru.study.currency_service.clients.interfaces.GifClient;
import ru.study.currency_service.clients.pojos.CurrencyResponse;
import ru.study.currency_service.clients.pojos.GifObject;
import ru.study.currency_service.controllers.CurrencyController;
import ru.study.currency_service.services.interfaces.CurrencyService;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDate.now;
import static java.time.ZoneId.of;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest
class CurrenciesServiceApplicationTests {
	private static final String EMPTY_STR = "";
	private static final String TOO_SHORT_STR = "aa";
	private static final String TOO_LONG_STR = "abcd";
	private static final String LOWERCASE_STR = "abc";
	private static final String STR_WITH_DIGITS = "2ab";
	private static final String STR_WITH_NON_LETTER_NON_DIGIT_SYMBOLS = "$ab";

	private static final URI GIF_URI = URI.create("example.com/");
	private static final byte[] GIF_BYTE_ARRAY = new byte[15];

	private static final String TARGET_CURRENCY_CODE = "ANG";
	private static final String BASE_CURRENCY_CODE = "TES";

	@Value("${api.currency.zoneId}")
	private String zoneId;

	private LocalDate yesterday;

	@Value("${api.currency.appId}")
	private String currencyAppId;

	@Value("${api.gif.appId}")
	private String gifAppId;

	@Value("${api.gif.good-performance-search-word}")
	private String upTagWord;

	@Value("${api.gif.bad-performance-search-word}")
	private String downTagWord;

	@MockBean
	private GifClient gifClient;

	@MockBean
	private CurrencyClient currencyClient;

	@Autowired
	CurrencyController currencyController;

	@Autowired
	CurrencyService currencyService;

	@BeforeEach
	public void setUp() {
		yesterday = now(of(zoneId)).minus(1, DAYS);

		// one should rewrite base currency code to get rig of the case where test target currency is equal to base currency from .yml
		setField(currencyService, "baseCurrencyCode", BASE_CURRENCY_CODE);
		setField(currencyController, "baseCurrencyCode", BASE_CURRENCY_CODE);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void targetCurrencyMakesBadPerformance_ReturnsBrokeGif() {
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

		when(currencyClient.getLatest(currencyAppId)).thenReturn(todayCurrencyResponse);
		when(currencyClient.getByDate(yesterday, currencyAppId)).thenReturn(yesterdayCurrencyResponse);

		GifObject returnedGifObject = new GifObject(GIF_URI);
		when(gifClient.getRandomGifObjectByTag(gifAppId, downTagWord)).thenReturn(returnedGifObject);


		when(gifClient.getGifByUrl(GIF_URI)).thenReturn(GIF_BYTE_ARRAY);

		assertEquals(GIF_BYTE_ARRAY, currencyController.getGifByCurrencyPerformance(TARGET_CURRENCY_CODE));
	}

	@Test
	void targetCurrencyMakesGoodPerformance_ReturnsRichGif() {
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

		when(currencyClient.getLatest(currencyAppId)).thenReturn(todayCurrencyResponse);
		when(currencyClient.getByDate(yesterday, currencyAppId)).thenReturn(yesterdayCurrencyResponse);

		GifObject returnedGifObject = new GifObject(GIF_URI);
		when(gifClient.getRandomGifObjectByTag(gifAppId, upTagWord)).thenReturn(returnedGifObject);


		when(gifClient.getGifByUrl(GIF_URI)).thenReturn(GIF_BYTE_ARRAY);

		assertEquals(GIF_BYTE_ARRAY, currencyController.getGifByCurrencyPerformance(TARGET_CURRENCY_CODE));
	}
}
