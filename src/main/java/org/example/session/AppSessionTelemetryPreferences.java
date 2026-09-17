package org.example.session;

import org.example.calculations.DistancePreference;
import org.example.calculations.RoutingCriterion;

public class AppSessionTelemetryPreferences {
    private static AppSessionTelemetryPreferences instance;

    private DistancePreference distancePreference = DistancePreference.MEDIUM;
    private double customMinSpeed = 50.0;
    private double customMaxSpeed = 100.0;
    private RoutingCriterion routingCriterion = RoutingCriterion.MIN_DISTANCE;

    private double maxDriveHoursBeforeRest = 4.0;
    private double maxDriveHoursBeforeHotel = 12.0;

    private AppSessionTelemetryPreferences() {}

    public static synchronized AppSessionTelemetryPreferences getInstance() {
        if (instance == null) {
            instance = new AppSessionTelemetryPreferences();
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

    public double getMaxDriveHoursBeforeRest() {
        return maxDriveHoursBeforeRest;
    }

    public void setMaxDriveHoursBeforeRest(double maxDriveHoursBeforeRest) {
        this.maxDriveHoursBeforeRest = maxDriveHoursBeforeRest;
    }

    public double getMaxDriveHoursBeforeHotel() {
        return maxDriveHoursBeforeHotel;
    }

    public void setMaxDriveHoursBeforeHotel(double maxDriveHoursBeforeHotel) {
        this.maxDriveHoursBeforeHotel = maxDriveHoursBeforeHotel;
    }
}