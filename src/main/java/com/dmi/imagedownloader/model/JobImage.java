package com.dmi.imagedownloader.model;

import com.dmi.imagedownloader.controller.dto.DownloadDto;

import java.util.Objects;
import java.util.UUID;

public class JobImage {

    private final DownloadDto downloadDto;

    private final UUID jobId;

    public JobImage(final DownloadDto downloadDto, final UUID jobId){
        this.downloadDto = downloadDto;
        this.jobId = jobId;
    }

    public DownloadDto getDownloadDto() {
        return downloadDto;
    }

    public UUID getJobId() {
        return jobId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobImage jobImage = (JobImage) o;
        return Objects.equals(downloadDto, jobImage.downloadDto) && Objects.equals(jobId, jobImage.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(downloadDto, jobId);
    }

    @Override
    public String toString() {
        return "JobImage{" +
                "jobId=" + jobId +
                '}';
    }
}
