package com.seachange.healthandsafty.model;

import java.util.ArrayList;

/**
 * Created by kevinsong on 19/10/2017.
 */

public class Stats {

    private String minTime, maxTime;
    private Integer currentStreak, numberOfHazards, numScheduled, numCompleted, compliancePercentage, numOfDays;
    private ArrayList<Champion> champions;

    public Stats() {

    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getNumberOfHazards() {
        return numberOfHazards;
    }

    public void setNumberOfHazards(Integer numberOfHazards) {
        this.numberOfHazards = numberOfHazards;
    }

    public Integer getNumScheduled() {
        return numScheduled;
    }

    public void setNumScheduled(Integer numScheduled) {
        this.numScheduled = numScheduled;
    }

    public Integer getNumCompleted() {
        return numCompleted;
    }

    public void setNumCompleted(Integer numCompleted) {
        this.numCompleted = numCompleted;
    }

    public Integer getCompliancePercentage() {
        return compliancePercentage;
    }

    public void setCompliancePercentage(Integer compliancePercentage) {
        this.compliancePercentage = compliancePercentage;
    }

    public Integer getNumOfDays() {
        return numOfDays;
    }

    public void setNumOfDays(Integer numOfDays) {
        this.numOfDays = numOfDays;
    }

    public ArrayList<Champion> getChampions() {
        return champions;
    }

    public void setChampions(ArrayList<Champion> champions) {
        this.champions = champions;
    }

    public Champion getChampionById(int uId) {
        Champion champion = new Champion();
        if (champions !=null) {
            for (Champion c : champions) {
                if (c.getUserId() == uId) return c;
            }
        }
        return champion;
    }
}
