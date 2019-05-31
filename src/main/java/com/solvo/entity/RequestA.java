package com.solvo.entity;

public class RequestA implements IRequest {
    private Integer X;

    public RequestA(Integer x) {
        X = x;
    }

    @Override
    public Integer getX() {
        return X;
    }
}
