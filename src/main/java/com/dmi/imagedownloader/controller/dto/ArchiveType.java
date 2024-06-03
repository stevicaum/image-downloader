package com.dmi.imagedownloader.controller.dto;

public enum ArchiveType {
    NATURAL("natural"),
    ENHANCED("enhanced"),
    AEROSOL("aerosol"),
    CLOUD("cloud");
    private String value;

    ArchiveType(final String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
