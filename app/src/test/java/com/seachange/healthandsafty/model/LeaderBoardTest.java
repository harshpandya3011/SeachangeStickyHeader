package com.seachange.healthandsafty.model;

import junit.framework.TestCase;

import org.junit.Assert;

public class LeaderBoardTest extends TestCase {

    private LeaderBoard leaderBoard;

    protected void setUp() throws Exception {
        super.setUp();
        leaderBoard = new LeaderBoard("test", 1.0, 2, 3.0,5.0,7);
    }

    public void testName() {
        String expected = "test";
        String actual = leaderBoard.getName();
        Assert.assertEquals(expected, actual);
    }

    public void testScore() {
        Double expected = 1.0;
        Double actual = leaderBoard.getScore();
        Assert.assertEquals(expected, actual);
    }

    public void testCheckCompliance() {
        Double expected = 3.0;
        Double actual = leaderBoard.getCheckCompliance();
        Assert.assertEquals(expected, actual);
    }

    public void testTourCompliance() {
        Double expected = 5.0;
        Double actual = leaderBoard.getTourCompliance();
        Assert.assertEquals(expected, actual);
    }

    public void testStatus() {
        int expected = 2;
        int actual = leaderBoard.getStatusRange();
        Assert.assertEquals(expected, actual);
    }

    public void testTourHazards() {
        int expected = 7;
        int actual = leaderBoard.getTourHazard();
        Assert.assertEquals(expected, actual);
    }
}