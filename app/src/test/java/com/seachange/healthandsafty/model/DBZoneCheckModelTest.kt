package com.seachange.healthandsafty.model

import org.junit.Assert
import org.junit.Test

class DBZoneCheckModelTest {

    @Test
    @Throws(Exception::class)
    fun isCurrentZoneCheckTest() {
        val checkModel = DBZoneCheckModel()
        Assert.assertEquals(false,
                checkModel.isCurrentZoneCheck("404", "9bb90e89-5034-4cac-802f-634b22649322"))
    }

    @Test
    @Throws(Exception::class)
    fun isCurrentZoneCheckTimeIdTest() {
        val checkModel = DBZoneCheckModel()
        checkModel.scheduledZoneCheckTimeId = "9bb90e89-5034-4cac-802f-634b22649322"
        Assert.assertEquals("9bb90e89-5034-4cac-802f-634b22649322",
                checkModel.scheduledZoneCheckTimeId)
    }

    @Test
    @Throws(Exception::class)
    fun addZoneCheckTest() {
        val checkModel = DBZoneCheckModel()
        val  startCommands = DBCheckStart()
        checkModel.addStartZoneCheckCommands(startCommands)
        Assert.assertEquals(1,
                checkModel.startZoneCheckCommands.size)

    }

    @Test
    @Throws(Exception::class)
    fun isZoneCheckCompleteTest() {
        val checkModel = DBZoneCheckModel()

        Assert.assertEquals(false,
                checkModel.isComplete)

    }

    @Test
    @Throws(Exception::class)
    fun isZoneCheckSyncedTest() {
        val checkModel = DBZoneCheckModel()

        Assert.assertEquals(false,
                checkModel.isSync)

    }

    @Test
    @Throws(Exception::class)
    fun getZoneCheckHazardsTest() {
        val checkModel = DBZoneCheckModel()

        Assert.assertEquals(0,
                checkModel.addressHazardCommandModelsV2.size)

    }

    @Test
    @Throws(Exception::class)
    fun getResumeZoneCheckCommandsTest() {
        val checkModel = DBZoneCheckModel()
        checkModel.resumeZoneCheckCommands = ArrayList()
        Assert.assertEquals(0,
                checkModel.resumeZoneCheckCommands.size)

    }

    @Test
    @Throws(Exception::class)
    fun getPauseZoneCheckCommandsTest() {
        val checkModel = DBZoneCheckModel()
        checkModel.pauseZoneCheckCommands = ArrayList()
        Assert.assertEquals(0,
                checkModel.pauseZoneCheckCommands.size)

    }

    @Test
    @Throws(Exception::class)
    fun getStartZoneCheckCommandsTest() {
        val checkModel = DBZoneCheckModel()
        checkModel.startZoneCheckCommands = ArrayList()

        Assert.assertEquals(0,
                checkModel.startZoneCheckCommands.size)

    }
}