package com.seachange.healthandsafty.model;

import org.parceler.Parcel;

@Parcel
public class RiskCategory {

    String title, subTitle, iconUrl, id;
    Boolean selected = false;

    public RiskCategory() {

    }

    public RiskCategory(String mTitle, String mSubTitle) {
        this.title = mTitle;
        this.subTitle = mSubTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
