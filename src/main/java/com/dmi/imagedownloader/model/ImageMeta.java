package com.dmi.imagedownloader.model;

import java.util.UUID;

public class ImageMeta {
    private UUID jobId;
    private String url;
    private String targetFile;
    private boolean markDone;


    public ImageMeta(final boolean markDone){
        this.markDone = markDone;
    }


    public ImageMeta(final boolean markDone, UUID jobId) {
        this.markDone = markDone;
        this.jobId = jobId;
    }

    public ImageMeta(final String url, final String targetFile){
        this.url = url;
        this.targetFile = targetFile;
    }


    public String getTargetFile() {
        return targetFile;
    }

    public String getUrl() {
        return url;
    }

    public boolean isMarkDone() {
        return markDone;
    }

    public UUID getJobId() {
        return jobId;
    }

    @Override
    public String toString() {
        return "ImageMeta{" +
                "url='" + url + '\'' +
                '}';
    }
}
