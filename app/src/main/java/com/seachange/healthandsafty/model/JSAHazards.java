package com.seachange.healthandsafty.model;

/**
 * Created by kevinsong on 05/12/2017.
 */

public class JSAHazards {

    private int number;
    private String contentImage, riskImage, title, control, level;

    public JSAHazards(){

    }

    public JSAHazards(int num, String contentImg, String riskImg, String mTitle, String mControl, String mLevel){

        this.number = num;
        this.contentImage = contentImg;
        this.riskImage = riskImg;
        this.title = mTitle;
        this.control = mControl;
        this.level = mLevel;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public String getRiskImage() {
        return riskImage;
    }

    public void setRiskImage(String riskImage) {
        this.riskImage = riskImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

}
