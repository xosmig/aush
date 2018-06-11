package com.xosmig.swdesignhw.aush.token;

public interface Token {
    <T>T accept(TokenVisitor<T> visitor);
    String backToString();
}
