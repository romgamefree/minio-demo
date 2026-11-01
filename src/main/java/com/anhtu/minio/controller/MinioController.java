package com.anhtu.minio.controller;

import com.anhtu.minio.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/minio")
public class MinioController {

    @Autowired
    private MinioService minioService;

    @PostMapping("/put-object")
    public ResponseEntity<String> putObject(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(minioService.putObjects(file));
        } catch (Exception e) {
            throw new RuntimeException("Co loi xay ra khi put object");
        }
    }

    @PostMapping(value = "/put-objects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> putObjects(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty() || files.stream().anyMatch(MultipartFile::isEmpty)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<String> objectNames = new ArrayList<>(files.size());
            for (MultipartFile file : files) {
                objectNames.add(minioService.putObjects(file));
            }
            return ResponseEntity.ok(objectNames);
        } catch (Exception e) {
            throw new RuntimeException("Co loi xay ra khi put nhieu object", e);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeObject(@RequestParam("objectName") String objectName) {
        try {
            minioService.removeObject(objectName);
            return ResponseEntity.ok("Deleted object");
        } catch (Exception e) {
            throw new RuntimeException("Co loi xay ra khi remove object");
        }
    }

    @GetMapping("/get-object-file")
    public ResponseEntity<String> getObject(@RequestParam("objectName") String objectName) {
        try {
            return ResponseEntity.ok(minioService.presignedUrl(objectName));
        } catch (Exception e) {
            throw new RuntimeException("Co loi xay ra khi lay URL object", e);
        }
    }
}
