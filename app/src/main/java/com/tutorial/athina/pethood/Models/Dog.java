package com.tutorial.athina.pethood.Models;

public class Dog {

    private String dogName, dogOwner, dogBreed, dogAge, dogColor, dogSize, dogMateFlag;

    public Dog() {
    }

    public Dog(String dogName, String dogOwner, String dogBreed, String dogAge, String dogColor, String dogSize, String dogMateFlag) {
        this.dogName = dogName;
        this.dogOwner = dogOwner;
        this.dogBreed = dogBreed;
        this.dogAge = dogAge;
        this.dogColor = dogColor;
        this.dogSize = dogSize;
        this.dogMateFlag = dogMateFlag;
    }

    public String getDogName() {
        return dogName;
    }

    public String getDogOwner() {
        return dogOwner;
    }

    public String getDogBreed() {
        return dogBreed;
    }

    public String getDogAge() {
        return dogAge;
    }

    public String getDogColor() {
        return dogColor;
    }

    public String getDogSize() {
        return dogSize;
    }

    public String getDogMateFlag() {
        return dogMateFlag;
    }
}
