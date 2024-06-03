package com.dmi.imagedownloader.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDto {
        private String image;

        protected ImageDto(){

        }

        // Getter and setter for date
        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }


}
