package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegionTest {

    @Test
    void shouldHaveTwelveRegions() {
        assertEquals(12, Region.values().length);
    }

    @Test
    void unitedStatesHasCorrectFields() {
        Region us = Region.UNITED_STATES;
        assertEquals("US", us.getCountryCode());
        assertEquals("en-US", us.getLocaleCode());
        assertEquals("$", us.getCurrencySymbol());
        assertEquals("United States", us.getDisplayName());
    }

    @Test
    void ukUsesGBCountryCode() {
        assertEquals("GB", Region.UNITED_KINGDOM.getCountryCode());
        assertEquals("£", Region.UNITED_KINGDOM.getCurrencySymbol());
    }

    @Test
    void euCountriesAllUseEuro() {
        assertEquals("€", Region.GERMANY.getCurrencySymbol());
        assertEquals("€", Region.FRANCE.getCurrencySymbol());
        assertEquals("€", Region.ITALY.getCurrencySymbol());
        assertEquals("€", Region.SPAIN.getCurrencySymbol());
        assertEquals("€", Region.EU_GENERAL.getCurrencySymbol());
    }

    @Test
    void asianRegionsHaveCorrectCurrencies() {
        assertEquals("¥", Region.JAPAN.getCurrencySymbol());
        assertEquals("S$", Region.SINGAPORE.getCurrencySymbol());
        assertEquals("HK$", Region.HONG_KONG.getCurrencySymbol());
        assertEquals("₩", Region.SOUTH_KOREA.getCurrencySymbol());
    }
}
