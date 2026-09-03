package org.example.core;

public class VirtualPoint extends PointOfInterest {
    public VirtualPoint(String referenceName, double x, double y) {
        super("Boundary [" + referenceName + "]", "", x, y);

    }

    @Override
    public String getType() {
        return "VIRTUAL_POINT";
    }
}
