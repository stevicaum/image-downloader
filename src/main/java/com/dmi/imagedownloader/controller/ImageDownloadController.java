package com.dmi.imagedownloader.controller;

import com.dmi.imagedownloader.controller.dto.DownloadDto;
import com.dmi.imagedownloader.exception.FieldValidationException;
import com.dmi.imagedownloader.model.StateType;
import com.dmi.imagedownloader.service.ImageDownloaderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping(ImageDownloadController.IMAGES_DOWNLOAD_URL)
@Tag(name = ImageDownloadController.IMAGES_DOWNLOAD_URL)
public class ImageDownloadController {

    public static final String IMAGES_DOWNLOAD_URL = "/images-download";
    private final ImageDownloaderService imageDownloaderService;

    public ImageDownloadController(final ImageDownloaderService imageDownloaderService) {
        this.imageDownloaderService = imageDownloaderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UUID initiateDownload(@Valid @RequestBody final DownloadDto downloadDto) {
        if(!isValidFolder(downloadDto.getTargetFolder())){
            throw new FieldValidationException("Target folder not valid:"+downloadDto.getTargetFolder());
        }
        return imageDownloaderService.processImages(downloadDto);
    }

    public boolean isValidFolder(String pathString) {
        Path path = Paths.get(pathString);
        Path parent = path.getParent();
        if (parent == null) {
            return false;
        }
        if (!Files.exists(parent)) {
            return false;
        }
        if (!Files.isDirectory(parent)) {
            return false;
        }
        return Files.isWritable(parent);
    }

    @GetMapping("/states/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StateType getState(@PathVariable final UUID id) {
        return imageDownloaderService.getState(id);
    }

    @GetMapping("/states")
    @ResponseStatus(HttpStatus.OK)
    public Map<UUID, StateType> getState() {
        return imageDownloaderService.getStates();
    }

}
