package com.ineffa.wondrouswilds.entities;

public enum NestTransitionType {
    ENTER("EnterNest"),
    EXIT("ExitNest"),
    PEEK("StartPeekingFromNest"),
    UNPEEK("StopPeekingFromNest");

    public final String animationName;

    NestTransitionType(String animationName) {
        this.animationName = animationName;
    }
}
