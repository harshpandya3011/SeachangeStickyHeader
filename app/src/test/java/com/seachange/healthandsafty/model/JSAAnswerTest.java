package com.seachange.healthandsafty.model;

import junit.framework.TestCase;

import org.junit.Assert;

public class JSAAnswerTest extends TestCase {

    private JSAAnswer jsaAnswer;

    protected void setUp() throws Exception {
        super.setUp();
        jsaAnswer = new JSAAnswer();
    }

    public void testAnswer() {
        String expected = "this is a test answer";
        jsaAnswer.setAnswer(expected);
        String actual = jsaAnswer.getAnswer();
        Assert.assertEquals(expected, actual);
    }

    public void testIsChecked() {
        Boolean expected = true;
        jsaAnswer.setChecked(expected);
        Boolean actual = jsaAnswer.isChecked();
        Assert.assertEquals(expected, actual);
    }
}
