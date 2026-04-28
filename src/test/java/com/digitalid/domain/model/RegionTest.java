package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegionTest {

    @Test
    void shouldHaveTwelveRegions() {
        assertEquals(12, Region.values().length);
    }

    @Test
    void unitedStatesShouldHaveCorrectFields() {
        Region us = Region.UNITED_STATES;
        assertEquals("US", us.getCountryCode());
        assertEquals("en-US", us.getLocaleCode());
        assertEquals("$", us.getCurrencySymbol());
        assertEquals("United States", us.getDisplayName());
    }

    @Test
    void unitedKingdomShouldHaveCorrectFields() {
        Region uk = Region.UNITED_KINGDOM;
        assertEquals("GB", uk.getCountryCode());
        assertEquals("£", uk.getCurrencySymbol());
    }

    @Test
    void japanShouldHaveCorrectLocale() {
        Region jp = Region.JAPAN;
        assertEquals("ja-JP", jp.getLocaleCode());
        assertEquals("¥", jp.getCurrencySymbol());
    }

    @Test
    void euRegionsShouldShareEuroCurrency() {
        assertEquals("€", Region.GERMANY.getCurrencySymbol());
        assertEquals("€", Region.FRANCE.getCurrencySymbol());
        assertEquals("€", Region.ITALY.getCurrencySymbol());
        assertEquals("€", Region.SPAIN.getCurrencySymbol());
        assertEquals("€", Region.EU_GENERAL.getCurrencySymbol());
    }

    @Test
    void valueOfShouldResolveCorrectly() {
        assertEquals(Region.SINGAPORE, Region.valueOf("SINGAPORE"));
        assertEquals(Region.HONG_KONG, Region.valueOf("HONG_KONG"));
        assertEquals(Region.SOUTH_KOREA, Region.valueOf("SOUTH_KOREA"));
    }
}
