package com.example.demo.repository;

import com.example.demo.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByCategoryType(String categoryType);
}
