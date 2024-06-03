package com.dmi.imagedownloader.config;

import com.dmi.imagedownloader.model.ImageMeta;
import com.dmi.imagedownloader.model.JobImage;
import com.dmi.imagedownloader.model.StateType;
import com.dmi.imagedownloader.service.DownloadExecutor;
import com.dmi.imagedownloader.service.ImageDownloaderService;
import com.dmi.imagedownloader.service.JobFetcherService;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Configuration
public class ImageDownloadConfiguration {

    private final BlockingQueue<ImageMeta> imageQueue = new ArrayBlockingQueue<>(100000);
    private final BlockingQueue<JobImage> jobImageQueue = new ArrayBlockingQueue<>(100);
    private final Map<UUID, StateType> statesMap = new Hashtable<>();

    @Value("${number.of.threads.image.download}")
    private int numberOfThreadsImageDownload;
    @Value("${number.of.threads.job.fetcher}")
    private int numberOfThreadsJobFetcher;

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(30000))
                .setReadTimeout(Duration.ofMillis(30000))
                .build();
    }

    @Bean
    public Boolean init1(final RestTemplate restTemplate){
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreadsImageDownload);
        for (int i = 0; i < numberOfThreadsImageDownload; i++) {
            executorService.execute(new DownloadExecutor(imageQueue, statesMap, restTemplate));
        }
        return false;
    }

    @Bean
    public Boolean init2(final RestTemplate restTemplate){
        ExecutorService executorServiceProducer = Executors.newFixedThreadPool(numberOfThreadsJobFetcher);
        for (int i = 0; i < numberOfThreadsJobFetcher; i++) {
            executorServiceProducer.execute(new JobFetcherService(jobImageQueue, statesMap, restTemplate, imageQueue));
        }
        return true;
    }


    @Bean
    public ImageDownloaderService downloadService() {
        return new ImageDownloaderService(jobImageQueue, statesMap);
    }


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI();
    }
}
