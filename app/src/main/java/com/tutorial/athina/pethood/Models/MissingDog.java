package com.tutorial.athina.pethood.Models;

public class MissingDog {

    String sender , message;

    public MissingDog() {
    }

    public MissingDog(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
