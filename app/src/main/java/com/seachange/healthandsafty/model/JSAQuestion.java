package com.seachange.healthandsafty.model;

import java.util.ArrayList;

/**
 * Created by kevinsong on 13/12/2017.
 */

public class JSAQuestion {

    private String question;
    private ArrayList<JSAAnswer> answers;

    public JSAQuestion(){

    }

    public JSAQuestion (String mQuestion, ArrayList<JSAAnswer> answerList) {
        this.question = mQuestion;
        this.answers = answerList;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<JSAAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<JSAAnswer> answers) {
        this.answers = answers;
    }
}
