package com.dmi.imagedownloader.controller;

import com.dmi.imagedownloader.controller.dto.ArchiveType;
import com.dmi.imagedownloader.controller.dto.DownloadDto;
import com.dmi.imagedownloader.controller.dto.ImageType;
import com.dmi.imagedownloader.service.ImageDownloaderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static com.dmi.imagedownloader.controller.dto.DownloadDto.DATE_FORMATTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImageDownloadController.class)
public class ImageDownloadControllerIT {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @MockBean
    private ImageDownloaderService imageDownloaderService;


    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void post400() throws Exception {
        LocalDate localDate = LocalDate.parse("2024-05-28", DATE_FORMATTER);
        DownloadDto downloadDto = new DownloadDto(localDate,"/Users/test*Document/images-download",
                ArchiveType.NATURAL, ImageType.JPG);
        String json = objectMapper.writeValueAsString(downloadDto);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(ImageDownloadController.IMAGES_DOWNLOAD_URL ).contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isBadRequest()).andReturn();
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void post400Empty() throws Exception {
        LocalDate localDate = LocalDate.parse("2024-05-28", DATE_FORMATTER);
        DownloadDto downloadDto = new DownloadDto(localDate,
                "", ArchiveType.NATURAL, ImageType.JPG);
        String json = objectMapper.writeValueAsString(downloadDto);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(ImageDownloadController.IMAGES_DOWNLOAD_URL ).contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isBadRequest()).andReturn();
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void post200() throws Exception {
        LocalDate localDate = LocalDate.parse("2024-05-28", DATE_FORMATTER);
        DownloadDto downloadDto = new DownloadDto(localDate,"/tmp/download/",
                ArchiveType.NATURAL, ImageType.JPG);
        String json = objectMapper.writeValueAsString(downloadDto);
        UUID reponseId = UUID.randomUUID();
        when(imageDownloaderService.processImages(any())).thenReturn(reponseId);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(ImageDownloadController.IMAGES_DOWNLOAD_URL ).contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk()).andReturn();
        assertNotNull(result.getResponse().getContentAsString());
        assertEquals(reponseId, UUID.fromString(objectMapper.readValue(result.getResponse().getContentAsString(), String.class)));
    }
}
