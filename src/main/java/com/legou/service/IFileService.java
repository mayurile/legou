package com.legou.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2017/11/7.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
