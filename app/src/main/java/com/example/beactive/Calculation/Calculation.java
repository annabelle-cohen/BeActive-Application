package com.example.beactive.Calculation;

import java.io.Serializable;

public class Calculation implements Serializable {
    public static final double CALORIE_PER_STEP=0.04;
    public static final double KILOMETER_PER_STEP=0.0003048;
    private double caloriesAccordingSteps=0;
    private double kilometersAccordingSteps=0;
    private int steps=0;

    public Calculation(double caloriesAccordingSteps,double kilometersAccordingSteps) {
        this.caloriesAccordingSteps = caloriesAccordingSteps;
        this.kilometersAccordingSteps = kilometersAccordingSteps;
    }

    public double calculateCalories(){
        caloriesAccordingSteps = steps*CALORIE_PER_STEP;
        return caloriesAccordingSteps;
    }

    public double calculateKilometers(){
        kilometersAccordingSteps = steps*KILOMETER_PER_STEP;
        return kilometersAccordingSteps;
    }

    public double getCaloriesAccordingSteps() {
        return caloriesAccordingSteps;
    }

    public void setCaloriesAccordingSteps(double caloriesAccordingSteps) {
        this.caloriesAccordingSteps = caloriesAccordingSteps;
    }

    public double getKilometersAccordingSteps() {
        return kilometersAccordingSteps;
    }

    public void setKilometersAccordingSteps(double kilometersAccordingSteps) {
        this.kilometersAccordingSteps = kilometersAccordingSteps;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
