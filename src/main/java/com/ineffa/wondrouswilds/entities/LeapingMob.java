package com.ineffa.wondrouswilds.entities;

public interface LeapingMob {


    boolean isLeaping();

    void setLeaping(boolean leaping, boolean retainPath);

    double getMaxLeapVelocity();

    int getMaxLeapHeight();
}
