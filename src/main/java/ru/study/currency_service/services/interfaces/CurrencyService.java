package ru.study.currency_service.services.interfaces;

public interface CurrencyService {
    enum PerformanceResult {
        UP, DOWN
    }

    /**
     * Compares today performance of provided currency with yesterday performance.
     *
     * @param targetCurrencyCode code of the target currency
     * @return <ul>
     * <li>{@code PerformanceResult.UP}, if today currency value is more than yesterday;</li>
     * <li>{@code PerformanceResult.DOWN} otherwise.</li>
     * </ul>
     */
    PerformanceResult getCurrencyPerformance(String targetCurrencyCode);
}
