package com.dmi.imagedownloader.service;

import com.dmi.imagedownloader.controller.dto.DownloadDto;
import com.dmi.imagedownloader.model.JobImage;
import com.dmi.imagedownloader.model.StateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ImageDownloaderService {
    private static final Logger log = LoggerFactory.getLogger( ImageDownloaderService.class );
    private final BlockingQueue<JobImage> jobImageQueue;
    private final Map<UUID, StateType> statesMap;
    public ImageDownloaderService(final BlockingQueue<JobImage> jobImageQueue, final Map<UUID, StateType> statesMap) {
        this.jobImageQueue = jobImageQueue;
        this.statesMap = statesMap;
    }

    public UUID processImages(DownloadDto downloadDto) {
        try {
            UUID jobUUID  = UUID.randomUUID();
            jobImageQueue.put(new JobImage(downloadDto, jobUUID));
            statesMap.put(jobUUID, StateType.PENDING);
            return jobUUID;
        } catch (InterruptedException e) {
            log.error("Put to job image queue failed {}", downloadDto, e);
            throw new RuntimeException("Job image queue put failed for:"+ downloadDto);
        }
    }

    public StateType getState(UUID id) {
        return statesMap.get(id);
    }

    public Map<UUID, StateType> getStates() {
        return statesMap;
    }
}
