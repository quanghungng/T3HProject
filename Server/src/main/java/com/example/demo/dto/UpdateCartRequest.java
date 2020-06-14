package com.example.demo.dto;

public class UpdateCartRequest {
    private String id;
    private Integer number;
    private Integer type;
    // Type 1: add
    // Type 2: Delete

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
