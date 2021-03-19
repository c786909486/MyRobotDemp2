package com.axun.myrobotdemp2;

import java.util.Objects;

/**
 * @author kzcai
 * @packageName com.axun.myrobotdemp2
 * @date 3/10/21
 */
public class RobotPosition {

    private float x;
    private float y;
    private float z;
    private float rotation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotPosition that = (RobotPosition) o;
        return Float.compare(that.x, x) == 0 &&
                Float.compare(that.y, y) == 0 &&
                Float.compare(that.z, z) == 0 &&
                Float.compare(that.rotation, rotation) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, rotation);
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

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
