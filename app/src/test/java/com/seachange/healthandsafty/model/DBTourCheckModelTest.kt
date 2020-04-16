package com.seachange.healthandsafty.model

import org.junit.Assert
import org.junit.Test

class DBTourCheckModelTest {

    @Test
    @Throws(Exception::class)
    fun isCurrentZoneCheckTest() {
        val checkModel = DBTourCheckModel()
        Assert.assertEquals(null,
                checkModel.getExistingTourCheck(404))
    }

    @Test
    @Throws(Exception::class)
    fun isCurrentZoneCheckTourIdTest() {
        val checkModel = DBTourCheckModel()
        checkModel.siteTourId = "sdfads93jkf9w"
        Assert.assertEquals("sdfads93jkf9w",
                checkModel.siteTourId)
    }

    @Test
    @Throws(Exception::class)
    fun isCurrentZoneCheckGroupIdTest() {
        val checkModel = DBTourCheckModel()
        checkModel.groupId = 99
        Assert.assertEquals(99,
                checkModel.groupId)
    }

    @Test
    @Throws(Exception::class)
    fun isCurrentZoneCheckStartTimeTest() {
        val checkModel = DBTourCheckModel()
        checkModel.timeStarted = "11/11/2019"
        Assert.assertEquals("11/11/2019",
                checkModel.timeStarted)
    }

    @Test
    @Throws(Exception::class)
    fun isCurrentZoneCheckEndTimeTest() {
        val checkModel = DBTourCheckModel()
        checkModel.timeCompleted = "11/11/2019"
        Assert.assertEquals("11/11/2019",
                checkModel.timeCompleted)
    }
}