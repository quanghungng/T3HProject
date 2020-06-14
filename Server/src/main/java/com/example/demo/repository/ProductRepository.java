package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameContains(String name);
    List<Product> findByCategoryAndNameContainsAndCategoryType(String category, String name, String categoryType);
}
