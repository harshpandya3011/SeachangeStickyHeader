package com.seachange.healthandsafty.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.seachange.healthandsafty.model.Champion;
import com.seachange.healthandsafty.model.JSAHazards;
import com.seachange.healthandsafty.model.JSAModel;
import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.RiskCategory;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.model.Stats;

/**
 * Created by kevinsong on 13/10/2017.
 */

public class JsonParser {

    public JsonParser() {

    }

    public ArrayList<SiteCheck> getSiteTourChecks(JSONObject object) {
        ArrayList<SiteCheck> checks = new ArrayList<>();
        checks.add(getSiteTourCheck(object));
        return checks;
    }

    public SiteCheck getSiteTourCheck(JSONObject object) {

        SiteCheck check = new SiteCheck();
        try {
            check.setDate(object.getJSONObject("id").getString("date"));
            //site tour is not updated yet...
            //need to update to site tour
            //don't forget...

            if (object.has("siteTour")) {
                if(object.get("siteTour") != null && object.get("siteTour") instanceof JSONObject) {
                    JSONObject tmp = object.getJSONObject("siteTour");

                    if (tmp.has("isComplete")) {
                        check.setComplete(tmp.getBoolean("isComplete"));
                    }

                    if (tmp.get("scheduledSiteTourId") !=null) {
                        check.setScheduledSiteTourId(tmp.getString("scheduledSiteTourId"));
                    }

                    if (tmp.get("id") !=null) {
                        check.setSiteTourId(tmp.getString("id"));
                    }

                    if (tmp.get("numOfHazardsIdentified") != null && tmp.get("numOfHazardsIdentified") instanceof Integer) {
                        check.setNumOfHazardsIdentified(tmp.getInt("numOfHazardsIdentified"));
                    }

                    if (tmp.get("timeStarted") !=null) {
                        check.setTimeStarted(tmp.getString("timeStarted"));
                    }

                    if (tmp.get("timeCompleted") !=null) {
                        check.setTimeCompleted(tmp.getString("timeCompleted"));
                    }
                    if (tmp.get("dateScheduled") !=null) {
                        check.setDate(tmp.getString("dateScheduled"));
                    }

                    if (tmp.get("siteId") !=null && tmp.get("numOfHazardsIdentified") instanceof Integer) {
                        check.setSiteId(tmp.getInt("siteId"));
                    }

                    JSONArray caygoTourCheckes = tmp.getJSONArray("zoneChecks");
                    if (caygoTourCheckes != null) {
                        check.setTourChecks(getTourChecks(caygoTourCheckes));
                    }
                }
            }
            //zone check times
            JSONArray array = object.getJSONArray("zoneCheckTimes");
            ArrayList<CheckTime> checkTimes = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                checkTimes.add(getCheckTimes(array.getJSONObject(i)));
            }
            check.setCheckTimes(checkTimes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return check;
    }


    private CheckTime getCheckTimes(JSONObject object) {

        CheckTime checkTimes = new CheckTime();
        try {
            checkTimes.setCheck_id(object.getString("id"));
            checkTimes.setCheck_date(object.getString("dateTime"));
            checkTimes.setStart_time(object.getString("startTime"));
            checkTimes.setEnd_time(object.getString("endTime"));

            if (object.get("numOfScheduledZoneChecks") instanceof Integer) {
                checkTimes.setTotalChecks(object.getInt("numOfScheduledZoneChecks"));
            }

            if (object.get("numOfCompletedZoneChecks") instanceof Integer) {
                checkTimes.setTotalChecksComplete(object.getInt("numOfCompletedZoneChecks"));
            }

            if (object.get("compliancePercent") instanceof Double) {
                checkTimes.setPercentage(object.getInt("compliancePercent"));
            }

            if (object.get("numOfHazardsIdentified") instanceof Integer) {
                checkTimes.setNumOfHazardsIdentified(object.getInt("numOfHazardsIdentified"));
            }

            JSONArray array = object.getJSONArray("zoneChecks");
            ArrayList<SiteZone> scheduledChecks = this.getScheduledChecks(array);
            checkTimes.setScheduledCheckses(scheduledChecks);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return checkTimes;
    }

    private ArrayList<SiteZone> getScheduledChecks(JSONArray array) {

        ArrayList<SiteZone> scheduledChecks = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {

                JSONObject jsonObject = array.getJSONObject(i);
                SiteZone check = new SiteZone();
                check.setStatus(jsonObject.getInt("status"));
                check.setZoneCheckId(jsonObject.getString("zoneCheckId"));
                //
                //is complete
                //
                check.setChecked(jsonObject.getBoolean("isComplete"));

                //zone data
                check.setZone_name(jsonObject.getString("zoneName"));
                check.setZone_id(jsonObject.getInt("zoneId"));
                if (jsonObject.get("numOfHazardsIdentified") != null && jsonObject.get("numOfHazardsIdentified") instanceof Integer){
                    check.setHazards(jsonObject.getInt("numOfHazardsIdentified"));
                } else {
                    check.setHazards(0);
                }
                scheduledChecks.add(check);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return scheduledChecks;
    }

    private ArrayList<SiteZone> getTourChecks(JSONArray array) {

        ArrayList<SiteZone> tourChecks = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {

                JSONObject jsonObject = array.getJSONObject(i);
                SiteZone check = new SiteZone();
                check.setChecked(jsonObject.getBoolean("isComplete"));
                check.setZone_name(jsonObject.getString("zoneName"));
                check.setZone_id(jsonObject.getInt("zoneId"));
                check.setStatus(jsonObject.getInt("status"));
                check.setZoneCheckId(jsonObject.getString("zoneCheckId"));

                if (jsonObject.get("numOfHazardsIdentified") != null && jsonObject.get("numOfHazardsIdentified") instanceof Integer){
                    check.setHazards(jsonObject.getInt("numOfHazardsIdentified"));
                } else {
                    check.setHazards(0);
                }
                if (jsonObject.has("timeCompleted") && jsonObject.get("timeCompleted") != null && jsonObject.get("timeCompleted") instanceof String) {
                    check.setTimeCompleted(jsonObject.getString("timeCompleted"));
                }

                tourChecks.add(check);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tourChecks;
    }

    public Stats getTourStats(JSONObject jsonObject) {

        Stats stats = new Stats();
        if (jsonObject == null) return stats;
        try {
            JSONObject tour = jsonObject.getJSONObject("siteTourSummary");
            JSONObject zone = jsonObject.getJSONObject("zoneCheckSummary");

            if (tour.has("complianceSummary") && tour.get("complianceSummary") !=null && tour.get("complianceSummary") instanceof JSONObject) {
                if (tour.get("numOfHazards") !=null && tour.get("numOfHazards") instanceof Integer) {
                    stats.setNumberOfHazards(tour.getInt("numOfHazards"));
                }
            }

            if (zone.has("complianceSummary") && zone.get("complianceSummary") !=null && zone.get("complianceSummary") instanceof JSONObject) {

                JSONObject tmp = zone.getJSONObject("complianceSummary");

                if (tmp.get("compliance") != null && tmp.get("compliance") instanceof Double) {
                    stats.setCompliancePercentage((int)Math.round(tmp.getDouble("compliance")));
                }

                if (tmp.get("numScheduled") !=null && tmp.get("numScheduled") instanceof Integer) {
                    stats.setNumScheduled(tmp.getInt("numScheduled"));
                }

                if (tmp.get("numCompleted") !=null && tmp.get("numCompleted") instanceof Integer) {
                    stats.setNumCompleted(tmp.getInt("numCompleted"));
                }
            }

            if (jsonObject.has("champions") && jsonObject.get("champions") instanceof JSONArray) {
                JSONArray array = jsonObject.getJSONArray("champions");
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Champion>>() {
                }.getType();
                ArrayList<Champion> arrayList = gson.fromJson(array.toString(), type);
                stats.setChampions(arrayList);
            }

            stats.setMinTime(jsonObject.getString("startDate"));
            stats.setMaxTime(jsonObject.getString("endDate"));
            stats.setNumOfDays(jsonObject.getInt("numOfDays"));

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return stats;
    }


    public JSAModel getSiteJsa(JSONObject object) {

        JSAModel jsaModel = new JSAModel();
        try {
            jsaModel.setTitle(object.getString("title"));
            jsaModel.setCreator(object.getString("creator"));
            jsaModel.setDate(object.getString("riskAssessmentDate"));
            jsaModel.setPrimaryImage(object.getString("primaryImageSourcePath"));
            jsaModel.setSecondaryImage(object.getString("secondaryImageSourcePath"));
            jsaModel.setGroupImage(object.getString("groupImageSourcePath"));
            jsaModel.setName(object.getJSONObject("site").getString("name"));
            jsaModel.setJsaHazards(this.getJSAHazards(object.getJSONArray("hazards")));
        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsaModel;
    }

    private ArrayList<JSAHazards> getJSAHazards(JSONArray array) {

        ArrayList<JSAHazards> jsaHazards = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {

                JSONObject jsonObject = array.getJSONObject(i);
                JSAHazards hazards = new JSAHazards();
                hazards.setNumber(jsonObject.getInt("number"));
                hazards.setContentImage(jsonObject.getString("primaryImageSourcePath"));
                hazards.setLevel(jsonObject.getJSONObject("pilo").getString("severity"));
                JSONObject risk = jsonObject.getJSONObject("risk");
                hazards.setTitle(risk.getString("title"));
                hazards.setControl(risk.getString("control"));
                hazards.setRiskImage(risk.getJSONObject("riskIcon").getString("primaryImageSourcePath"));

                jsaHazards.add(hazards);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsaHazards;
    }

    public ArrayList<RiskCategory> getRiskCategories(JSONArray array) {
        ArrayList<RiskCategory> arrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                RiskCategory riskCategory = new RiskCategory();
                riskCategory.setTitle(jsonObject.getString("label"));
                riskCategory.setId(jsonObject.getString("id"));
                riskCategory.setIconUrl(jsonObject.getString("iconUrl"));
                JSONObject risk = jsonObject.getJSONObject("riskCategory");
                riskCategory.setSubTitle(risk.getString("label"));
                arrayList.add(riskCategory);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
