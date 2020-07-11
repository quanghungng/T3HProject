package com.example.demo.controller;

import com.example.demo.service.StoreFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/link/")
public class LinkController {
    @Autowired
    private StoreFileService storeFileService;

//    load file
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storeFileService.loadFile(filename);
        MediaType type = MediaType.ALL;

//        check file type image dạng gì
        if(filename.toLowerCase().endsWith("png")) {
            type = MediaType.IMAGE_PNG;
        }else if(filename.toLowerCase().endsWith("jpg") || filename.toLowerCase().endsWith("jpeg")) {
            type = MediaType.IMAGE_JPEG;
        }else if(filename.toLowerCase().endsWith("gif")) {
            type = MediaType.IMAGE_GIF;
        }

        return ResponseEntity.ok().contentType(type)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filenames=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
