package com.dmi.imagedownloader.service;

import com.dmi.imagedownloader.controller.dto.ArchiveType;
import com.dmi.imagedownloader.controller.dto.DownloadDto;
import com.dmi.imagedownloader.controller.dto.ImageType;
import com.dmi.imagedownloader.model.ImageMeta;
import com.dmi.imagedownloader.model.JobImage;
import com.dmi.imagedownloader.model.StateType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.dmi.imagedownloader.controller.dto.DownloadDto.DATE_FORMATTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImageDownloaderServiceTest {
    private ObjectMapper objectMapper;
    private ImageDownloaderService imageDownloaderService;
    private final BlockingQueue<ImageMeta> imageQueue = new ArrayBlockingQueue<>(100000);
    private final BlockingQueue<JobImage> jobImageQueue = new ArrayBlockingQueue<>(100);
    private final Map<UUID, StateType> statesMap = new Hashtable<>();

    @BeforeEach
    void init(){
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        imageDownloaderService = new ImageDownloaderService(jobImageQueue, statesMap);
    }

    @Test
    void processImages(){
        LocalDate localDate = LocalDate.parse("2024-05-28", DATE_FORMATTER);
        DownloadDto downloadDto = new DownloadDto(localDate,"/tmp/download/",
                ArchiveType.NATURAL, ImageType.JPG);
        assertEquals(0,statesMap.size());
        assertEquals(0,jobImageQueue.size());
        UUID id = imageDownloaderService.processImages(downloadDto);
        assertNotNull(id);
        assertEquals(1,statesMap.size());
        assertEquals(1,jobImageQueue.size());
        assertEquals(StateType.PENDING, statesMap.get(id));
        assertEquals(new JobImage(downloadDto, id), jobImageQueue.poll());
    }
}
