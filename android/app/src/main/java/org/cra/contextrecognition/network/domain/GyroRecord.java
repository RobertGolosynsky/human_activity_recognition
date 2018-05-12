package org.cra.contextrecognition.network.domain;

import java.io.Serializable;

public class GyroRecord implements Serializable{
    private float x;
    private float y;
    private float z;

    public GyroRecord(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "GyroRecord{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
