package com.example.service;

import org.springframework.web.multipart.MultipartFile;

public interface ILogoStorageService {

    IImageUrlPath makeImageUrlPath(String url);

    IImageUrlPath makeImageUrlPath(String nom, int version);

    public String save(MultipartFile logo);
}
