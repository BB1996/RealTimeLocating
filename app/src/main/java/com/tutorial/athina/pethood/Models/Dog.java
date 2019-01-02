package com.tutorial.athina.pethood.Models;

public class Dog {

    private String dogName, dogOwner, dogBreed, dogAge, dogColor, dogSize, dogMateFlag, dogPhoto;

    public Dog() {
    }



    public Dog(String dogName, String dogOwner, String dogBreed, String dogAge, String dogColor, String dogSize, String dogMateFlag, String dogPhoto) {
        this.dogName = dogName;
        this.dogOwner = dogOwner;
        this.dogBreed = dogBreed;
        this.dogAge = dogAge;
        this.dogColor = dogColor;
        this.dogSize = dogSize;
        this.dogMateFlag = dogMateFlag;
        this.dogPhoto = dogPhoto;

    }
    public String getDogPhoto() {
        return dogPhoto;
    }

    public void setDogPhoto(String dogPhoto) {
        this.dogPhoto = dogPhoto;
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

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public void setDogOwner(String dogOwner) {
        this.dogOwner = dogOwner;
    }

    public void setDogBreed(String dogBreed) {
        this.dogBreed = dogBreed;
    }

    public void setDogAge(String dogAge) {
        this.dogAge = dogAge;
    }

    public void setDogColor(String dogColor) {
        this.dogColor = dogColor;
    }

    public void setDogSize(String dogSize) {
        this.dogSize = dogSize;
    }

    public void setDogMateFlag(String dogMateFlag) {
        this.dogMateFlag = dogMateFlag;
    }
}
