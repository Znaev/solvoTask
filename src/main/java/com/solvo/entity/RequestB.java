package com.solvo.entity;

public class RequestB implements IRequest{
    private Integer X;

    public RequestB(Integer x) {
        X = x;
    }

    public Integer getX() {
        return X;
    }
}
