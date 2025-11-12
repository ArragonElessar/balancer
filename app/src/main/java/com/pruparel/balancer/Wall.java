package com.pruparel.balancer;

import android.graphics.Color;

public class Wall {

    public Vector3D<Float> startPos, endPos; // Wall Start and End
    public float strokeWidth;

    public Wall( Vector3D<Float> startPos, Vector3D<Float> endPos){
        this.startPos = startPos;
        this.endPos = endPos;
    }

}
