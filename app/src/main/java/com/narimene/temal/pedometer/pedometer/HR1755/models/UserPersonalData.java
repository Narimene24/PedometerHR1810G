package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class UserPersonalData {

    private int gender;
    private int age;
    private int height;
    private int weight;
    private int strideLength;

    public UserPersonalData(){};

    public static UserPersonalData getInstance(int gender, int age, int height, int weight, int strideLength){
        return new UserPersonalData(gender, age, height, weight, strideLength);
    }

    public UserPersonalData(int gender, int age, int height, int weight, int strideLength) {
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.strideLength = strideLength;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStrideLength() {
        return strideLength;
    }

    public void setStrideLength(int strideLength) {
        this.strideLength = strideLength;
    }

    @Override
    public String toString() {

        String type  = "";
        if (gender == 1){
            type = "type mal";
        }else {
            type = "type female";
        }

        return "User{"               +
                " gender="           + type +
                ", age="             + age +
                " Years, height="    + height +
                " Cm, weight="       + weight +
                " Kg, strideLength=" + strideLength +
                " Cm}";
    }
}
