package com.example.comepaga.model.user;

public enum AccessType {

    RESTRICTED,
    ACCESS_GRANTED,
    ADMINISTRADOR("0"),
    CLIENTE("1"),
    REPARTIDOR("2");

    private String value;

    AccessType(String value){
        this.value = value;
    }

    AccessType() {

    }

    public String getValue(){
        return this.value;
    }
}
