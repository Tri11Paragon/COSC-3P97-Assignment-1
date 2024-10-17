package com.mouseboy.assignment1.helpers;

// numbers are special tokens which store data. It is annoying you can't store data inside enums
public class Number implements Token {

    public final String value;

    public Number(String value) {
        this.value = value;
    }

}
