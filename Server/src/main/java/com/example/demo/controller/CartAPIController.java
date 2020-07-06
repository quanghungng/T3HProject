package com.example.demo.controller;

import com.example.demo.constant.Code;
import com.example.demo.constant.Message;
import com.example.demo.dto.BaseResponse;
import com.example.demo.dto.PlaceOrderRequest;
import com.example.demo.dto.UpdateCartRequest;
import com.example.demo.model.*;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthenRequestAPIService;
import com.example.demo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

    @RestController
    @RequestMapping("/cart")
    public class CartAPIController {

        @Autowired
        UserRepository userRepository;
        @Autowired
        ProductRepository productRepository;

        @Autowired
        TokenService tokenService;

        @Autowired
        AuthenRequestAPIService authenRequestAPIService;

        @Autowired
        JavaMailSender javaMailSender;

        // Get all product in cart of user
        @RequestMapping("/products/{id}")
        public BaseResponse getListProductInCart(@PathVariable("id") String id){
            BaseResponse response = new BaseResponse();
            Optional<Users> optionalUsers = userRepository.findById(id);
            if(!optionalUsers.isPresent()){
                response.setCode(Code.NOT_FOUND);
                response.setMessage(Message.NOT_FOUND);
                response.setData(null);
                return response;
            }else{
                Cart exitsCart = optionalUsers.get().getCart();
                response.setCode(Code.SUCCESS);
                response.setMessage(Message.GET_DATA_SUCCESS);
                response.setData(exitsCart);
            }
            return response;
        }

        // Update List product in Cart
        @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
        public BaseResponse updateCart(@PathVariable("id") String id,@RequestHeader("token") String token, @RequestBody UpdateCartRequest updateCartRequest ){
            BaseResponse response = new BaseResponse();

            if(authenRequestAPIService.checkValidOfTokenAndId(token,id).equals("Invalid token")){
                response.setCode(Code.NOT_FOUND);
                response.setMessage("Token is invalid");
                response.setData(null);
                return response;
            }
            if(authenRequestAPIService.checkValidOfTokenAndId(token,id).equals("Invalid token")){
                response.setCode(Code.NOT_FOUND);
                response.setMessage("Request is not allowed");
                response.setData(null);
                return response;
            }
            Optional<Users> optionalUsers = userRepository.findById(id);

            // Check whether id of user is available
            if(!optionalUsers.isPresent()){
                response.setCode(Code.NOT_FOUND);
                response.setMessage(Message.NOT_FOUND);
                response.setData(null);
                return response;
            }
            // Check whether id of request is available in product table
            Optional<Product> exitsProduct = productRepository.findById(updateCartRequest.getId());
            if (!exitsProduct.isPresent()){
                response.setCode(Code.NOT_FOUND);
                response.setMessage(Message.NOT_FOUND);
                response.setData(null);
                return response;
            }
            // Check whether number of request is valid
            if(updateCartRequest.getNumber() == null || updateCartRequest.getNumber() < 0) {
                response.setCode(Code.INVALID_DATA);
                response.setMessage(Message.INVALID_DATA);
                response.setData(null);
                return response;
            }
            Users user = optionalUsers.get();
            Cart exitsCart = user.getCart();
            List<Product> productListInCart = exitsCart.getListProduct();

            // Add
            if(updateCartRequest.getType()==1) {
                boolean found = false;
                if (productListInCart.isEmpty()) {
                    Product product = exitsProduct.get();
                    product.setQuantity(updateCartRequest.getNumber());
                    productListInCart.add(product);
                    response.setCode(Code.SUCCESS);
                    response.setMessage(Message.EDIT_DATA_SUCCESS);
                    response.setData(null);
                } else {
                    for (Product p : productListInCart) {
                        if (p.getId().equals(updateCartRequest.getId())) {
                            p.setQuantity(p.getQuantity() + updateCartRequest.getNumber());
                            response.setCode(Code.SUCCESS);
                            response.setMessage(Message.EDIT_DATA_SUCCESS);
                            response.setData(null);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Product product = exitsProduct.get();
                        product.setQuantity(updateCartRequest.getNumber());
                        productListInCart.add(product);
                        response.setCode(Code.SUCCESS);
                        response.setMessage(Message.EDIT_DATA_SUCCESS);
                        response.setData(exitsCart);
                    }
                }
                exitsCart.setListProduct(productListInCart);
            }
            // Delete
            else if(updateCartRequest.getType()==2) {
                // Check whether id of product exits in cart and number is valid
                boolean found = false;
                for (Product p : productListInCart) {
                    if (p.getId().equals(updateCartRequest.getId())) {
                        if (p.getQuantity() < updateCartRequest.getNumber()) {
                            response.setCode(Code.INVALID_DATA);
                            response.setMessage(Message.INVALID_DATA);
                            response.setData(null);
                            return response;
                        } else if (p.getQuantity() > updateCartRequest.getNumber()) {
                            p.setQuantity(p.getQuantity() - updateCartRequest.getNumber());
                        } else if (p.getQuantity() == updateCartRequest.getNumber()) {
                            productListInCart.remove(p);
                        }
                        found = true;
                        break;
                    }
                }
                System.out.println(found);
                if (!found) {
                    response.setCode(Code.NOT_FOUND);
                    response.setMessage(Message.NOT_FOUND);
                    response.setData(null);
                    return response;
                }
                exitsCart.setListProduct(productListInCart);
                user.setCart(exitsCart);
                userRepository.save(user);

                response.setCode(Code.SUCCESS);
                response.setMessage(Message.EDIT_DATA_SUCCESS);
                response.setData(exitsCart);
            }
            else if(updateCartRequest.getType()==3) {
                boolean found = false;
                if (productListInCart.isEmpty()) {
                    Product product = exitsProduct.get();
                    product.setQuantity(updateCartRequest.getNumber());
                    productListInCart.add(product);
                    response.setCode(Code.SUCCESS);
                    response.setMessage(Message.EDIT_DATA_SUCCESS);
                    response.setData(null);
                } else {
                    for (Product p : productListInCart) {
                        if (p.getId().equals(updateCartRequest.getId())) {
                            if(updateCartRequest.getNumber()==0){
                                response.setCode(Code.SUCCESS);
                                response.setMessage(Message.EDIT_DATA_SUCCESS);
                                response.setData(null);
                                productListInCart.remove(p);
                                found = true;
                                break;
                            }
                            else {
                                p.setQuantity(updateCartRequest.getNumber());
                                response.setCode(Code.SUCCESS);
                                response.setMessage(Message.EDIT_DATA_SUCCESS);
                                response.setData(null);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        Product product = exitsProduct.get();
                        product.setQuantity(updateCartRequest.getNumber());
                        productListInCart.add(product);
                        response.setCode(Code.SUCCESS);
                        response.setMessage(Message.EDIT_DATA_SUCCESS);
                        response.setData(exitsCart);
                    }
                }
                exitsCart.setListProduct(productListInCart);
            }
            user.setCart(exitsCart);
            userRepository.save(user);
            return response;
        }
        @RequestMapping(value = "checkout/{id}", method = RequestMethod.POST)
        public BaseResponse checkout(@RequestBody PlaceOrderRequest placeOrderRequest, @RequestHeader("token") String token, @PathVariable("id") String id) throws MessagingException {
            BaseResponse response = new BaseResponse();

            if(authenRequestAPIService.checkValidOfTokenAndId(token,id).equals("Invalid token")){
                response.setCode(Code.NOT_FOUND);
                response.setMessage("Token is invalid");
                response.setData(null);
                return response;
            }
            if(authenRequestAPIService.checkValidOfTokenAndId(token,id).equals("Invalid token")){
                response.setCode(Code.NOT_FOUND);
                response.setMessage("Request is not allowed");
                response.setData(null);
                return response;
            }
            Optional<Users> optionalUsers = userRepository.findById(id);

            // Check whether id of user is available
            if(!optionalUsers.isPresent()){
                response.setCode(Code.NOT_FOUND);
                response.setMessage(Message.NOT_FOUND);
                response.setData(null);
                return response;
            }

            Users user = optionalUsers.get();

            // Check request
            if(!placeOrderRequest.getType().equals("default") && !placeOrderRequest.getType().equals("different")){
                response.setCode(Code.INVALID_DATA);
                response.setMessage(Message.INVALID_DATA);
                response.setData(null);
                return response;
            }

            // Check if nothing in cart
            if(user.getCart().getListProduct().size() <=0){
                response.setCode(Code.INVALID_DATA);
                response.setMessage("Nothing in your cart!");
                response.setData(null);
                return response;
            }
            if(placeOrderRequest.getType().equals("default")){
                Order order = new Order();
                order.setCart(user.getCart());
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setName(user.getName());
                orderInfo.setAddress(user.getAddress());
                orderInfo.setPhone(user.getPhone());
                orderInfo.setNote("");
                order.setOrderInfo(orderInfo);
                order.setStatus("processing");
                List<Order> listOrder = user.getListOrder();
                listOrder.add(order);
                user.setListOrder(listOrder);

                // clear cart
                Cart newCart = new Cart();
                newCart.setListProduct(new ArrayList<>());
                user.setCart(newCart);
                userRepository.save(user);

                response.setCode(Code.SUCCESS);
                response.setMessage("Success");
                response.setData(user);

                MimeMessage message = javaMailSender.createMimeMessage();
                boolean multipart = true;
                MimeMessageHelper helper = new MimeMessageHelper(message, multipart, "utf-8");
                String htmlMsg ="";
                htmlMsg += "<h1> Successfully Ordered!</h1> <h2> Bill Information </h2> <strong>Name: </strong><p>" + user.getName()+ "</p><br><strong>Phone: </strong><p>" + user.getPhone() + "</p><br><strong>Address: </strong><p>" + user.getAddress() + "</p><br>";
            htmlMsg += "<table  style=\"border: 1px solid deeppink; width: 70%;text-align: center;background-color: lightpink; \">\n" +
                        "\t\t<tr>\n" +
                        "\t\t\t<th>No.</th>\n" +
                        "\t\t\t<th>Product Name</th>\n" +
                        "\t\t\t<th>Quantity</th>\n" +
                        "\t\t\t<th>Unit Price</th>\n" +
                        "\t\t\t<th>Price</th>\n" +
                        "\t\t</tr>\n";
                int i = 1;
                float total =0;
                for (Product p: order.getCart().getListProduct()
                     ) {
                    htmlMsg += "<tr><td>" + i++ +"</td><td>" + p.getName() + "</td><td>" + p.getQuantity() + "</td><td>" + p.getPrice() + "</td><td>" + p.getQuantity()*p.getPrice()+ "</td></tr>";
                    total += p.getQuantity()*p.getPrice();
                }

                htmlMsg +=
                        "\t\t<tr  style=\"background-color: deeppink;\">\n" +
                                "\t\t\t<td style=\"color: antiquewhite; font-weight: bold;\" colspan=\"4\">Shipping</td>\n" +
                                "\t\t\t<td>  "    +   "free" +  "        </td>\n" +
                                "\t\t</tr>\n" ;
                htmlMsg +=
                        "\t\t<tr  style=\"background-color: deeppink;\">\n" +
                        "\t\t\t<td style=\"color: antiquewhite; font-weight: bold;\" colspan=\"4\">Total</td>\n" +
                        "\t\t\t<td>  "    +   total +  "        </td>\n" +
                        "\t\t</tr>\n" +
                        "\t</table>";

                htmlMsg += "<h3><i>Thanks for using our services</i></h3>";
                message.setContent(htmlMsg, "text/html; charset=utf-8");
                helper.setTo(user.getEmail());
                helper.setSubject("Success Order");
                javaMailSender.send(message);
            }
            if(placeOrderRequest.getType().equals("different")){
                if(placeOrderRequest.getAddress().isEmpty() ||
                        placeOrderRequest.getAddress().isEmpty() ||
                        placeOrderRequest.getName().isEmpty()||
                        placeOrderRequest.getPhone().isEmpty()){
                    response.setCode(Code.INVALID_DATA);
                    response.setMessage(Message.INVALID_DATA);
                    response.setData(null);
                    return response;
                }
                Order order = new Order();
                order.setCart(user.getCart());

                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setName(placeOrderRequest.getName());
                orderInfo.setAddress(placeOrderRequest.getAddress());
                orderInfo.setPhone(placeOrderRequest.getPhone());
                orderInfo.setNote(placeOrderRequest.getNote());
                order.setOrderInfo(orderInfo);
                order.setStatus("processing");
                List<Order> listOrder = user.getListOrder();
                listOrder.add(order);
                user.setListOrder(listOrder);

                // clear cart
                Cart newCart = new Cart();
                newCart.setListProduct(new ArrayList<>());
                user.setCart(newCart);
                userRepository.save(user);
                response.setCode(Code.SUCCESS);
                response.setMessage("Success");
                response.setData(user);
            }
            return response;
        }
    }

