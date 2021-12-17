package com.example.in4code.repos.result;

public class FailureResult implements Result{
    String message;

    public FailureResult(String message) {

        this.message = message;
    }

    @Override
    public boolean getType() {
        return false;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
