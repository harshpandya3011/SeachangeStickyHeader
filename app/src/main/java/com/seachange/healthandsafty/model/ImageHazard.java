package com.seachange.healthandsafty.model;

public class ImageHazard {

    private String hazardId, timeFound, imageId, riskId, riskName;

    public ImageHazard() {

    }

    public ImageHazard(String hId, String time, String imgId, String rId, String name) {
        this.hazardId = hId;
        this.timeFound = time;
        this.imageId = imgId;
        this.riskId = rId;
        this.riskName = name;
    }

    public String getHazardId() {
        return hazardId;
    }

    public void setHazardId(String hazardId) {
        this.hazardId = hazardId;
    }

    public String getTimeFound() {
        return timeFound;
    }

    public void setTimeFound(String timeFound) {
        this.timeFound = timeFound;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

    public String getRiskName() {
        return riskName;
    }

    public void setRiskName(String riskName) {
        this.riskName = riskName;
    }
}
