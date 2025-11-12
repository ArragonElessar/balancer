package com.pruparel.balancer;

import android.graphics.Color;

public class Ball {

    public float radius;
    public Vector3D<Float> position;
    public Vector3D<Float> velocity;
    public Vector3D<Float> acceleration;

    public Ball(float radius, Vector3D<Float> position){
        this.radius = radius;
        this.position = position;
        this.velocity = Vector3D.Zero;
        this.acceleration = Vector3D.Zero;
    }

}
