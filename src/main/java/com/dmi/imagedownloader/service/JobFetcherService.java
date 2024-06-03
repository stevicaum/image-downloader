package com.dmi.imagedownloader.service;

import com.dmi.imagedownloader.controller.dto.*;
import com.dmi.imagedownloader.model.ImageMeta;
import com.dmi.imagedownloader.model.JobImage;
import com.dmi.imagedownloader.model.StateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class JobFetcherService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(JobFetcherService.class);

    //https://epic.gsfc.nasa.gov/api/natural/all?api_key=DEMO_KEY
    private final static String URL_BASE = "https://api.nasa.gov/EPIC/api/%s/all?api_key=%s";

    //https://api.nasa.gov/EPIC/api/natural/date/2019-05-30?api_key=DEMO_KEY
    private final static String IMAGES_FOR_DATE = "https://api.nasa.gov/EPIC/api/%s/date/%s?api_key=%s";
    private final static String API_KEY = "p960B4skMQHGdPnetw2KYFVzzoomz4GV5oZMZjUM";

    //https://epic.gsfc.nasa.gov/archive/natural/2024/05/28/thumbs/epic_1b_20240528024729.jpg
    private final static String IMAGE_URL = "https://epic.gsfc.nasa.gov/archive/%s/%s/%s/%s/%s/%s.%s?api_key=%s";
    public static final String REGEX = "-";
    private final RestTemplate restTemplate;
    private final BlockingQueue<JobImage> jobImageQueue;
    private final Map<UUID, StateType> statesMap;
    private final BlockingQueue<ImageMeta> imageQueue;

    public JobFetcherService(final BlockingQueue<JobImage> jobImageQueue, final Map<UUID, StateType> statesMap,
                             final RestTemplate restTemplate, final BlockingQueue<ImageMeta> imageQueue) {
        this.jobImageQueue = jobImageQueue;
        this.statesMap = statesMap;
        this.restTemplate = restTemplate;
        this.imageQueue = imageQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobImage jobImage = jobImageQueue.take();
                log.info("JobFetcherService.take {}", jobImage);
                final String lastDate = resolveDate(jobImage.getDownloadDto());
                final ArchiveType archiveType = Optional.of(jobImage.getDownloadDto().getArchiveType()).orElse(ArchiveType.NATURAL);
                final ImageType imageType = Optional.of(jobImage.getDownloadDto().getImageType()).orElse(ImageType.PNG);
                final ImageDto[] imageDtos = getAllForDownload(String.format(IMAGES_FOR_DATE, archiveType.getValue(), lastDate, API_KEY));
                final List<ImageMeta> imageMetas = Arrays.stream(imageDtos).map(imageDto -> createImageMeta(imageDto, archiveType, lastDate, imageType, jobImage)).toList();
                final File file = new File( jobImage.getDownloadDto().getTargetFolder() +"/"+ lastDate);
                boolean result = file.mkdirs();
                if(result) {
                    statesMap.put(jobImage.getJobId(), StateType.IN_PROGRESS);
                    imageQueue.addAll(imageMetas);
                    imageQueue.add(new ImageMeta(Boolean.TRUE, jobImage.getJobId()));
                } else {
                    statesMap.put(jobImage.getJobId(), StateType.FAILED);
                    log.error("Job Execution failed cant create dir {}", file);
                }
            } catch (InterruptedException e) {
                log.error("Take from job image queue failed", e);
            }
        }
    }

    private ImageMeta createImageMeta(ImageDto imageDto, ArchiveType archiveType, String lastDate, ImageType imageType, JobImage jobImage) {
        final String[] lastDateArray = resolveDate(jobImage.getDownloadDto()).split(REGEX);
        return new ImageMeta(String.format(IMAGE_URL, archiveType.getValue(),
                lastDateArray[0], lastDateArray[1],
                lastDateArray[2], imageType.getFolder(),
                imageDto.getImage(), imageType.getExtension(), API_KEY),
                jobImage.getDownloadDto().getTargetFolder()+"/"+lastDate+"/"+imageDto.getImage()+"."+imageType.getExtension());
    }


    private ImageDto[] getAllForDownload(final String url) {
        ResponseEntity<ImageDto[]> responseEntity = restTemplate.getForEntity(url, ImageDto[].class);
        return responseEntity.getBody();
    }

    private String resolveDate(DownloadDto downloadDto) {
        String lastDate = null;
        if (downloadDto.getDate() == null) {
            String urlAllDates = String.format(URL_BASE, downloadDto.getArchiveType(), API_KEY);
            ResponseEntity<DateDto[]> responseEntity = restTemplate.getForEntity(urlAllDates, DateDto[].class);
            DateDto[] dateDtos = responseEntity.getBody();
            lastDate = dateDtos[dateDtos.length - 1].getDate();
        } else {
            lastDate = DownloadDto.DATE_FORMATTER.format(downloadDto.getDate());
        }
        return lastDate;
    }
}
