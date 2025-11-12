package com.pruparel.balancer;

public class Vector3D<T extends Number>{ // 1. Use bounded type for numeric operations

    public T x, y, z;
    public Vector3D( T xc, T yc, T zc){
        x = xc;y = yc;
        z = zc;
    }

    // A static "zero" vector for convenience
    public static final Vector3D<Float> Zero = new Vector3D<>(0.0f, 0.0f, 0.0f);

    // Add two vector3Ds
    @SuppressWarnings("unchecked") // Suppress warning for the necessary cast
    public static <T extends Number> Vector3D<T> add( Vector3D<T> a, Vector3D<T> b){
        // 2. Check the type of the components to perform the correct math
        if (a.x instanceof Double || b.x instanceof Double) {
            Double resX = a.x.doubleValue() + b.x.doubleValue();
            Double resY = a.y.doubleValue() + b.y.doubleValue();
            Double resZ = a.z.doubleValue() + b.z.doubleValue();
            return (Vector3D<T>) new Vector3D<>(resX, resY, resZ); // 3. Return a new vector with the summed components
        } else if (a.x instanceof Float || b.x instanceof Float) {
            Float resX = a.x.floatValue() + b.x.floatValue();
            Float resY = a.y.floatValue() + b.y.floatValue();
            Float resZ = a.z.floatValue() + b.z.floatValue();
            return (Vector3D<T>) new Vector3D<>(resX, resY, resZ);
        } else if (a.x instanceof Integer || b.x instanceof Integer) {
            Integer resX = a.x.intValue() + b.x.intValue();
            Integer resY = a.y.intValue() + b.y.intValue();
            Integer resZ = a.z.intValue() + b.z.intValue();
            return (Vector3D<T>) new Vector3D<>(resX, resY, resZ);
        }
        // Add other numeric types as needed (Long, etc.)

        // 4. Return null or throw an exception if the type is not supported
        throw new IllegalArgumentException("Unsupported number type for vector addition.");
    }

    // multiply a scalar with vector3Ds
    @SuppressWarnings("unchecked") // Suppress warning for the necessary cast
    public static <T extends Number> Vector3D<T> scalarProduct( Vector3D<T> a, T scalar){
        // 2. Check the type of the components to perform the correct math
        if (a.x instanceof Double || scalar instanceof Double) {
            Double resX = a.x.doubleValue() * scalar.doubleValue();
            Double resY = a.y.doubleValue() * scalar.doubleValue();
            Double resZ = a.z.doubleValue() * scalar.doubleValue();
            return (Vector3D<T>) new Vector3D<>(resX, resY, resZ); // 3. Return a new vector with the summed components
        } else if (a.x instanceof Float || scalar instanceof Float) {
            Float resX = a.x.floatValue() * scalar.floatValue();
            Float resY = a.y.floatValue() * scalar.floatValue();
            Float resZ = a.z.floatValue() * scalar.floatValue();
            return (Vector3D<T>) new Vector3D<>(resX, resY, resZ);
        } else if (a.x instanceof Integer || scalar instanceof Integer) {
            Integer resX = a.x.intValue() * scalar.intValue();
            Integer resY = a.y.intValue() * scalar.intValue();
            Integer resZ = a.z.intValue() * scalar.intValue();
            return (Vector3D<T>) new Vector3D<>(resX, resY, resZ);
        }
        // Add other numeric types as needed (Long, etc.)

        // 4. Return null or throw an exception if the type is not supported
        throw new IllegalArgumentException("Unsupported number type for vector addition.");
    }

    @Override
    public String toString() {
        return "Vector3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
