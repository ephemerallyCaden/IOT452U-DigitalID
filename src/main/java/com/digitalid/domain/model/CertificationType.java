package com.digitalid.domain.model;

public enum CertificationType {

    // Food Safety — United States
    US_FOOD_HANDLER(Region.UNITED_STATES, CertificationCategory.FOOD_SAFETY, "Food Handler Certificate", 36, "State Health Department"),
    US_SERVSAFE_MANAGER(Region.UNITED_STATES, CertificationCategory.FOOD_SAFETY, "ServSafe Manager (CFPM)", 60, "National Restaurant Association"),

    // Food Safety — United Kingdom
    UK_LEVEL_2_FOOD_SAFETY(Region.UNITED_KINGDOM, CertificationCategory.FOOD_SAFETY, "Level 2 Food Safety and Hygiene", 36, "CIEH / Highfield"),
    UK_LEVEL_3_FOOD_SAFETY(Region.UNITED_KINGDOM, CertificationCategory.FOOD_SAFETY, "Level 3 Food Safety (Supervising)", 36, "CIEH / Highfield"),

    // Food Safety — EU
    EU_HACCP(Region.EU_GENERAL, CertificationCategory.FOOD_SAFETY, "HACCP Training (EU 852/2004)", 36, "National Food Authority"),
    GERMANY_GESUNDHEITSZEUGNIS(Region.GERMANY, CertificationCategory.FOOD_SAFETY, "Gesundheitszeugnis", -1, "Gesundheitsamt"),
    GERMANY_HYGIENE_SCHULUNG(Region.GERMANY, CertificationCategory.FOOD_SAFETY, "Hygiene Schulung", -1, "IHK"),
    FRANCE_FORMATION_HACCP(Region.FRANCE, CertificationCategory.FOOD_SAFETY, "Formation HACCP", 36, "DRAAF"),
    ITALY_ATTESTATO_HACCP(Region.ITALY, CertificationCategory.FOOD_SAFETY, "Attestato HACCP", 30, "ASL"),
    SPAIN_CERTIFICADO_MANIPULADOR(Region.SPAIN, CertificationCategory.FOOD_SAFETY, "Certificado Manipulador de Alimentos", 48, "Consejeria de Sanidad"),

    // Food Safety — Asia
    SINGAPORE_FOOD_SAFETY_LEVEL_1(Region.SINGAPORE, CertificationCategory.FOOD_SAFETY, "Food Safety Level 1", 60, "Singapore Food Agency"),
    SINGAPORE_WSQ_FOOD_SAFETY(Region.SINGAPORE, CertificationCategory.FOOD_SAFETY, "WSQ Food Safety Course", 60, "Singapore Food Agency"),
    JAPAN_FOOD_SANITATION_MANAGER(Region.JAPAN, CertificationCategory.FOOD_SAFETY, "Food Sanitation Manager", -1, "Public Health Center"),
    HONG_KONG_BASIC_FOOD_HYGIENE(Region.HONG_KONG, CertificationCategory.FOOD_SAFETY, "Basic Food Hygiene Certificate", -1, "FEHD"),
    SOUTH_KOREA_FOOD_HYGIENE(Region.SOUTH_KOREA, CertificationCategory.FOOD_SAFETY, "Food Hygiene Education", 12, "Ministry of Food and Drug Safety"),
    CHINA_FOOD_SAFETY_TRAINING(Region.CHINA, CertificationCategory.FOOD_SAFETY, "Food Safety Training Certificate", 18, "Provincial Health Commission"),
    CHINA_HEALTH_CERTIFICATE(Region.CHINA, CertificationCategory.FOOD_SAFETY, "Health Certificate for Food Workers", 12, "Provincial Health Commission"),

    // Permits — UK
    UK_STREET_TRADING_LICENCE(Region.UNITED_KINGDOM, CertificationCategory.PERMIT, "Street Trading Licence", 12, "Local Authority"),

    // Background Checks — UK
    UK_DBS_CHECK(Region.UNITED_KINGDOM, CertificationCategory.BACKGROUND_CHECK, "DBS Check", 36, "Disclosure and Barring Service"),

    // Licences — UK
    UK_DRIVER_LICENCE(Region.UNITED_KINGDOM, CertificationCategory.LICENCE, "UK Driving Licence", -1, "DVLA"),

    // Training — UK
    UK_ALLERGEN_TRAINING(Region.UNITED_KINGDOM, CertificationCategory.TRAINING, "Allergen Awareness Training", 24, "FSA Approved Provider");

    private final Region homeRegion;
    private final CertificationCategory category;
    private final String displayName;
    private final int validityMonths; // -1 means lifetime
    private final String issuingAuthority;

    CertificationType(Region homeRegion, CertificationCategory category,
                      String displayName, int validityMonths, String issuingAuthority) {
        this.homeRegion = homeRegion;
        this.category = category;
        this.displayName = displayName;
        this.validityMonths = validityMonths;
        this.issuingAuthority = issuingAuthority;
    }

    public Region getHomeRegion() {
        return homeRegion;
    }

    public CertificationCategory getCategory() {
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValidityMonths() {
        return validityMonths;
    }

    public boolean isLifetime() {
        return validityMonths == -1;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }
}
