package com.digitalid.domain.model;

public enum CertificationType {

    // United States
    US_FOOD_HANDLER(Region.UNITED_STATES, "Food Handler Certificate", 36, "State Health Department"),
    US_SERVSAFE_MANAGER(Region.UNITED_STATES, "ServSafe Manager (CFPM)", 60, "National Restaurant Association"),
    US_MOBILE_VENDOR_PERMIT(Region.UNITED_STATES, "Mobile Food Vending Permit", 12, "City Health Department"),
    US_ALLERGEN_TRAINING(Region.UNITED_STATES, "Allergen Awareness Training", 24, "State Health Department"),

    // United Kingdom
    UK_LEVEL_2_FOOD_SAFETY(Region.UNITED_KINGDOM, "Level 2 Food Safety and Hygiene", 36, "CIEH / Highfield"),
    UK_LEVEL_3_FOOD_SAFETY(Region.UNITED_KINGDOM, "Level 3 Food Safety (Supervising)", 36, "CIEH / Highfield"),

    // European Union (general)
    EU_HACCP(Region.EU_GENERAL, "HACCP Training (EU 852/2004)", 36, "National Food Authority"),

    // Germany
    GERMANY_GESUNDHEITSZEUGNIS(Region.GERMANY, "Gesundheitszeugnis", -1, "Gesundheitsamt"),
    GERMANY_HYGIENE_SCHULUNG(Region.GERMANY, "Hygiene Schulung", -1, "IHK"),

    // France
    FRANCE_FORMATION_HACCP(Region.FRANCE, "Formation HACCP", 36, "DRAAF"),

    // Italy
    ITALY_ATTESTATO_HACCP(Region.ITALY, "Attestato HACCP", 30, "ASL"),

    // Spain
    SPAIN_CERTIFICADO_MANIPULADOR(Region.SPAIN, "Certificado Manipulador de Alimentos", 48, "Consejeria de Sanidad"),

    // Singapore
    SINGAPORE_FOOD_SAFETY_LEVEL_1(Region.SINGAPORE, "Food Safety Level 1", 60, "Singapore Food Agency"),
    SINGAPORE_WSQ_FOOD_SAFETY(Region.SINGAPORE, "WSQ Food Safety Course", 60, "Singapore Food Agency"),
    SINGAPORE_FOOD_HYGIENE_OFFICER(Region.SINGAPORE, "Food Hygiene Officer Certificate", 60, "Singapore Food Agency"),

    // Japan
    JAPAN_FOOD_SANITATION_MANAGER(Region.JAPAN, "Food Sanitation Manager", -1, "Public Health Center"),

    // Hong Kong
    HONG_KONG_BASIC_FOOD_HYGIENE(Region.HONG_KONG, "Basic Food Hygiene Certificate", -1, "FEHD"),

    // South Korea
    SOUTH_KOREA_FOOD_HYGIENE(Region.SOUTH_KOREA, "Food Hygiene Education", 12, "Ministry of Food and Drug Safety"),

    // China
    CHINA_FOOD_SAFETY_TRAINING(Region.CHINA, "Food Safety Training Certificate", 18, "Provincial Health Commission"),

    // China (additional)
    CHINA_HEALTH_CERTIFICATE(Region.CHINA, "Health Certificate for Food Workers", 12, "Provincial Health Commission");

    private final Region homeRegion;
    private final String displayName;
    private final int validityMonths; // -1 means lifetime
    private final String issuingAuthority;

    CertificationType(Region homeRegion, String displayName, int validityMonths, String issuingAuthority) {
        this.homeRegion = homeRegion;
        this.displayName = displayName;
        this.validityMonths = validityMonths;
        this.issuingAuthority = issuingAuthority;
    }

    public Region getHomeRegion() {
        return homeRegion;
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
