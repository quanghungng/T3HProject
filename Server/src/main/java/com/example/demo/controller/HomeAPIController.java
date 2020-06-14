package com.example.demo.controller;

import com.example.demo.constant.Code;
import com.example.demo.constant.Message;
import com.example.demo.dto.BaseResponse;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Token;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.AuthenRequestAPIService;
import com.example.demo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HomeAPIController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    AuthenRequestAPIService authenRequestAPIService;

    //Get all product
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public BaseResponse getAllProduct() {
        BaseResponse response = new BaseResponse();
        response.setCode(Code.SUCCESS);
        response.setMessage(Message.GET_DATA_SUCCESS);
        response.setData(productRepository.findAll());
        return response;
    }

    //Get product
    @RequestMapping(value = "/product/getProductByCategoryAndName", method = RequestMethod.GET)
    public BaseResponse getProductByName(@RequestParam("name") String name, @RequestParam("category") String category, @RequestParam("categoryType") String categoryType){
        BaseResponse response = new BaseResponse();

        if(category.equals("all")){
            List<Product> listProduct = productRepository.findByNameContains(name);
            response.setCode(Code.SUCCESS);
            response.setMessage(Message.SEARCH_DATA_SUCCESS);
            response.setData(listProduct);
            return response;
        }
        if(category.isEmpty() || categoryType.isEmpty()){
            response.setCode(Code.INVALID_DATA);
            response.setMessage(Message.INVALID_DATA);
            response.setData(null);
        }
        List<Product> listProduct = productRepository.findByCategoryAndNameContainsAndCategoryType(category,name,categoryType);
        if (listProduct.size() > 0) { // Name is available
            response.setCode(Code.SUCCESS);
            response.setMessage(Message.SEARCH_DATA_SUCCESS);
            response.setData(listProduct);
        } else { // Name is not found
            response.setCode(Code.NOT_FOUND);
            response.setMessage(Message.NOT_FOUND);
            response.setData(null);
        }
        return response;
    }
    @RequestMapping(value = "/product/getCategory", method = RequestMethod.GET)
    public BaseResponse getProductByName(){
        BaseResponse response = new BaseResponse();
        List<Category> categoryList = categoryRepository.findAll();
        response.setCode(Code.SUCCESS);
        response.setMessage(Message.GET_DATA_SUCCESS);
        response.setData(categoryList);
        return response;
    }
    @RequestMapping(value = "getinfo/{userID}", method = RequestMethod.GET)
    public BaseResponse getinfo(@PathVariable("userID") String userId,@RequestHeader("token") String token){
        BaseResponse response = new BaseResponse();
        String check = authenRequestAPIService.checkValidOfTokenAndId(token, userId);
        if(check.equals("Invalid token")){
            response.setCode(Code.NOT_FOUND);
            response.setMessage("Invalid Token");
            response.setData(null);
            return response;
        }
        if(check.equals("Not allowed")){
            response.setCode(Code.NOT_FOUND);
            response.setMessage("Do not allowed to request");
            response.setData(null);
            return response;
        }
        response.setCode(Code.SUCCESS);
        response.setMessage("Success get info of user having id: " + userId);
        response.setData(null);
        return response;
    }
}
