package com.example.demo;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        for(int i = 21; i<= 30; i++){
//            Product product = new Product();
//            product.setId(String.valueOf(i));
//            product.setName("Ao khoac" + String.valueOf(i));
//            product.setDescription("This is the product description");
//            product.setImage(String.valueOf(i));
//            product.setCategory("Maxi Dresses");
//            product.setPrice(i*10);
//            List<String> size = new ArrayList<>();
//            size.add("S");
//            size.add("M");
//            size.add("L");
//            size.add("XL");
//            product.setSize(size);
//            productRepository.save(product);
//        }
//        Category category = new Category();
//        category.setCategoryType("Woman");
//        category.setCategoryName("T-Shirt");
//        categoryRepository.save(category);
   }
}
