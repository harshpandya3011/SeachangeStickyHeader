package com.seachange.healthandsafty.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kevinsong on 05/12/2017.
 */

public class JSAModel {


    private String title, creator, date, primaryImage, secondaryImage, groupImage, name;

    ArrayList<JSAHazards> jsaHazards;

    public JSAModel(){

    }

    public JSAModel(String mTitle, String mCreator, String mDate, String mPrimaryImage, String mSecondaryImage, String mGroupImage, String mName){

        this.title = mTitle;
        this.creator = mCreator;
        this.date = mDate;
        this.primaryImage = mPrimaryImage;
        this.secondaryImage = mSecondaryImage;
        this.groupImage = mGroupImage;
        this.name = mName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public String getSecondaryImage() {
        return secondaryImage;
    }

    public void setSecondaryImage(String secondaryImage) {
        this.secondaryImage = secondaryImage;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<JSAHazards> getJsaHazards() {
        return jsaHazards;
    }

    public void setJsaHazards(ArrayList<JSAHazards> jsaHazards) {
        this.jsaHazards = jsaHazards;
    }

    public String getAssessmentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date mDate = sdf.parse(date);
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            return format.format(mDate);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
