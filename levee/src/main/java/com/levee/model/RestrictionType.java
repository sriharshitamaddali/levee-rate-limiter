package com.levee.model;

public enum RestrictionType {
    IP("IP"),
    USER("USER"),
    SERVICE("SERVICE");

    private final String restrictionType;

    RestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public String getRestrictionType() {
        return this.restrictionType;
    }
}
