package com.xosmig.swdesignhw.aush.token;

public interface Token {
    void accept(TokenVisitor visitor);
    String backToString();
}
