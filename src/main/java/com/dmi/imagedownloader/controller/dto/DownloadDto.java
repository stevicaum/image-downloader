package com.dmi.imagedownloader.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DownloadDto {

    public static final String ISO_DATE = "yyyy-MM-dd";
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE);

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE)
    private LocalDate date;

    @NotBlank(message = "This field can't be empty")
    private String targetFolder;

    private ArchiveType archiveType;

    private ImageType imageType;


    protected DownloadDto(){

    }

    public DownloadDto(LocalDate date, String targetFolder, ArchiveType archiveType, ImageType imageType){
        this.date = date;
        this.targetFolder = targetFolder;
        this.archiveType = archiveType;
        this.imageType = imageType;
    }


    public LocalDate getDate() {
        return date;
    }

    public String getTargetFolder() {
        return targetFolder;
    }

    public ArchiveType getArchiveType() {
        return archiveType;
    }

    public ImageType getImageType() {
        return imageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadDto that = (DownloadDto) o;
        return Objects.equals(date, that.date) && Objects.equals(targetFolder, that.targetFolder) && archiveType == that.archiveType && imageType == that.imageType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, targetFolder, archiveType, imageType);
    }

    @Override
    public String toString() {
        return "DownloadDto{" +
                "date=" + date +
                ", targetFolder='" + targetFolder + '\'' +
                ", archiveType=" + archiveType +
                ", imageType=" + imageType +
                '}';
    }

}
