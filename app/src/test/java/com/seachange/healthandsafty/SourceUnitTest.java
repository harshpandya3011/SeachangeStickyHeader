package com.seachange.healthandsafty;

import org.junit.Test;

import java.util.ArrayList;

import com.seachange.healthandsafty.model.HazardType;
import com.seachange.healthandsafty.fragment.HazardSourceFragment;

import static org.junit.Assert.assertEquals;

/**
 * Created by kevinsong on 11/01/2018.
 */

public class SourceUnitTest {

    @Test
    public void checkHazards() throws Exception {
        HazardSourceFragment mFragment = new HazardSourceFragment();

        ArrayList<HazardType> mHazardTypes = new ArrayList<>();
        HazardType type = new HazardType();
        type.setSelected(true);
        HazardType type2 = new HazardType();
        type2.setSelected(true);
        HazardType type3 = new HazardType();
        type3.setSelected(true);
        HazardType type4 = new HazardType();
        type4.setSelected(false);
        HazardType type5 = new HazardType();
        type5.setSelected(true);
        HazardType type6 = new HazardType();
        type6.setSelected(false);

        mHazardTypes.add(type);
        mHazardTypes.add(type2);
        mHazardTypes.add(type3);
        mHazardTypes.add(type4);
        mHazardTypes.add(type5);
        mHazardTypes.add(type6);

        mFragment.mHazardTypes = mHazardTypes;
        int tmp = mFragment.getCurrentHazards();
        assertEquals(4, tmp);

    }
}
