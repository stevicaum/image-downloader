package com.dmi.imagedownloader.controller.dto;

public enum ImageType {
    PNG("png", "png"),
    JPG("jpg", "jpg"),
    THUMBS("thumbs","jpg");
    private final String extension;
    private String folder;

    ImageType(final String folder, final String extension){
        this.folder = folder;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public String getFolder() {
        return folder;
    }
}
