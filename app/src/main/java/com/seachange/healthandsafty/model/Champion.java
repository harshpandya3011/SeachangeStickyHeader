package com.seachange.healthandsafty.model;

public class Champion {

    private Integer userId, rank, numOfZoneChecksCompleted;

    public Champion() {

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getNumOfZoneChecksCompleted() {
        return numOfZoneChecksCompleted;
    }

    public void setNumOfZoneChecksCompleted(Integer numOfZoneChecksCompleted) {
        this.numOfZoneChecksCompleted = numOfZoneChecksCompleted;
    }

}
