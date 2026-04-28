package com.digitalid.domain.model;

public enum Region {

    UNITED_STATES("US", "en-US", "$", "United States"),
    UNITED_KINGDOM("GB", "en-GB", "£", "United Kingdom"),
    GERMANY("DE", "de-DE", "€", "Germany"),
    FRANCE("FR", "fr-FR", "€", "France"),
    ITALY("IT", "it-IT", "€", "Italy"),
    SPAIN("ES", "es-ES", "€", "Spain"),
    EU_GENERAL("EU", "en-EU", "€", "European Union"),
    SINGAPORE("SG", "en-SG", "S$", "Singapore"),
    JAPAN("JP", "ja-JP", "¥", "Japan"),
    HONG_KONG("HK", "zh-HK", "HK$", "Hong Kong"),
    SOUTH_KOREA("KR", "ko-KR", "₩", "South Korea"),
    CHINA("CN", "zh-CN", "¥", "China");

    private final String countryCode;
    private final String localeCode;
    private final String currencySymbol;
    private final String displayName;

    Region(String countryCode, String localeCode, String currencySymbol, String displayName) {
        this.countryCode = countryCode;
        this.localeCode = localeCode;
        this.currencySymbol = currencySymbol;
        this.displayName = displayName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getDisplayName() {
        return displayName;
    }
}
