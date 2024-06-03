package com.dmi.imagedownloader.service;

import com.dmi.imagedownloader.model.ImageMeta;
import com.dmi.imagedownloader.model.StateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class DownloadExecutor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DownloadExecutor.class);
    private final BlockingQueue<ImageMeta> imageQueue;
    private final Map<UUID, StateType> statesMap;
    private final RestTemplate restTemplate;

    public DownloadExecutor(final BlockingQueue<ImageMeta> imageQueue, final Map<UUID, StateType> statesMap,
                            final RestTemplate restTemplate) {
        this.imageQueue = imageQueue;
        this.statesMap = statesMap;
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ImageMeta imageMeta = imageQueue.take();
                log.info("Take ImageMeta {}", imageMeta);
                if (imageMeta != null && imageMeta.isMarkDone()) {
                    statesMap.put(imageMeta.getJobId(), StateType.FINISHED);
                } else if (imageMeta != null) {
                    log.info("Downloading {}", imageMeta);
                    download(imageMeta, 0);
                }
            } catch (InterruptedException e) {
                log.error("Take from image queue failed", e);
            }
        }
    }

    private void download(ImageMeta imageMeta, int retry) {
        try {
            if(retry>2){
                return;
            }
            byte[] imageBytes = restTemplate.getForObject(imageMeta.getUrl(), byte[].class);
            Files.write(Paths.get(imageMeta.getTargetFile()), imageBytes);
        } catch (RestClientException e) {
            log.error("Error downloading image {}", imageMeta.getUrl(), e);
            retry++;
            download(imageMeta, retry);
        } catch (IOException e) {
            log.error("Cant create file {}", imageMeta.getTargetFile(), e);
        }
    }
}
