package com.shoppingapp.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shoppingapp.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product,String>{
    
}
