package org.example.calculations;

public class AppSessionCALC {
    private static AppSessionCALC instance;

    private DistancePreference distancePreference = DistancePreference.MEDIUM;
    private double customMinSpeed = 50.0;
    private double customMaxSpeed = 100.0;
    private RoutingCriterion routingCriterion = RoutingCriterion.MIN_DISTANCE;

    private AppSessionCALC() {}

    public static synchronized AppSessionCALC getInstance() {
        if (instance == null) {
            instance = new AppSessionCALC();
        }
        return instance;
    }

    public DistancePreference getDistancePreference() {
        return distancePreference;
    }

    public void setDistancePreference(DistancePreference distancePreference) {
        this.distancePreference = distancePreference;
    }

    public double getCustomMinSpeed() {
        return customMinSpeed;
    }

    public void setCustomMinSpeed(double customMinSpeed) {
        this.customMinSpeed = customMinSpeed;
    }

    public double getCustomMaxSpeed() {
        return customMaxSpeed;
    }

    public void setCustomMaxSpeed(double customMaxSpeed) {
        this.customMaxSpeed = customMaxSpeed;
    }

    public RoutingCriterion getRoutingCriterion() {
        return routingCriterion;
    }

    public void setRoutingCriterion(RoutingCriterion routingCriterion) {
        this.routingCriterion = routingCriterion;
    }
}
