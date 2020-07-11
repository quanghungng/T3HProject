package com.example.demo.controller;

import com.example.demo.model.Image;
import com.example.demo.repository.ImageRepository;
import com.example.demo.service.StoreFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImageContrloler {

    @Autowired
    private StoreFileService storeFileService;

    @Autowired
    ImageRepository imageRepository;

//    @RequestMapping("/index")
//    public String index() {
//        return "index";
//    }

//    save image vào database
    @RequestMapping("/upload/image")
    @ResponseBody
    public Image uploadImage(@RequestBody Image image) {
        if(image.getImage().isEmpty()) {
            return null;
        }
        Image exitImage = imageRepository.save(image);
        return exitImage;
    }

//
    @PostMapping("/upload")
    @ResponseBody
    public String singleFileUpload(@RequestParam("file") MultipartFile file) {
//        name image
        String fileName = "";
//        link url (Nếu thay đổi port thì phải thay đổi cải cái này nữa)
        String fileLink = "http://localhost:8082/link/";
        try{
            if(file.isEmpty()) {
                throw new Exception();
            }
//            get file name
            fileName = storeFileService.store(file);
//            cộng với url
            fileLink += fileName;
        }catch (Exception e){
            e.printStackTrace();
        }
        return fileLink;
    }
}
