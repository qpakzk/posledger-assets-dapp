package com.poscoict.assets.web.controller;

import com.poscoict.assets.web.ExceptionHandleController;


public class RootController extends ExceptionHandleController {
    private long counter;

    private String chaincodeId;

    public RootController() {
        counter = 100;
        chaincodeId = "assetscc0";
    }

    protected String getCounter() {
        return Long.toString(counter);
    }

    protected void incrementCounter() {
        counter++;
    }

    protected String getChaincodeId() {
        return chaincodeId;
    }
}
