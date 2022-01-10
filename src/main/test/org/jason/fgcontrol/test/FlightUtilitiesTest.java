package org.jason.fgcontrol.test;

import org.jason.fgcontrol.flight.util.FlightUtilities;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FlightUtilitiesTest {

    @Test
    public void testWithinHeadingThreshold() {
        //simple
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(40.0, 15.0, 46.0));
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(40.0, 15.0, 36.0));
        
        //at 90
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(90.0, 15.0, 80.0));
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(90.0, 15.0, 100.0));
        
        //threshold past zero
        //<0
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(350.0, 15.0, 0.0));
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(350.0, 20.0, 345.0));
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(350.0, 20.0, 5.0));
        Assert.assertFalse(FlightUtilities.withinHeadingThreshold(350.0, 10.0, 5.0));
        
        //>0
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(5.0, 355.0, 20.0));
        
        //at zero
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(0.0, 15.0, 0.0));
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(0.0, 15.0, 10.0));
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(0.0, 15.0, 350.0));
        
        //out of threshold
        Assert.assertFalse(FlightUtilities.withinHeadingThreshold(0.0, 15.0, 15.0));
        Assert.assertFalse(FlightUtilities.withinHeadingThreshold(0.0, 15.0, 15.0001));
        
        //small differences
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(40.0, 0.5, 40.3));
        Assert.assertTrue(FlightUtilities.withinHeadingThreshold(40.3, 0.5, 40.0));
    }
    
    @Test
    public void testHeadingComparator() {
        
        //simple cases
        //Assert.assertEquals(FlightUtilities.headingCompareTo(10.0, 15.0), FlightUtilities.HEADING_CW_ADJUST);
        //Assert.assertEquals(FlightUtilities.headingCompareTo(10.0, 10.0001), FlightUtilities.HEADING_CW_ADJUST);
        //Assert.assertEquals(FlightUtilities.headingCompareTo(10.0, 5.0), FlightUtilities.HEADING_CCW_ADJUST);
        //Assert.assertEquals(FlightUtilities.headingCompareTo(15.0, 10.0001), FlightUtilities.HEADING_CCW_ADJUST);
        //Assert.assertEquals(FlightUtilities.headingCompareTo(10.0, 10.0), FlightUtilities.HEADING_NO_ADJUST);
        
        //near 0 degs on circle
        Assert.assertEquals(FlightUtilities.headingCompareTo(350.0, 5.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(350.0, 0.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(0.0, 0.0001), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(0.0, 350.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(0.0, 0.0), FlightUtilities.HEADING_NO_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(360.0, 360.0), FlightUtilities.HEADING_NO_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(360.0, 0.0), FlightUtilities.HEADING_NO_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(0.0, 360.0), FlightUtilities.HEADING_NO_ADJUST);
        
        //with +COMPARATOR_REFERENCE_DEGREES past 0 on circle
        //330 degs + 90 degs => 60 degs
        Assert.assertEquals(FlightUtilities.headingCompareTo(331.0, 5.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(330.0, 5.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(329.0, 5.0), FlightUtilities.HEADING_CW_ADJUST);

        Assert.assertEquals(FlightUtilities.headingCompareTo(330.0, 331.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(330.0, 359.0), FlightUtilities.HEADING_CW_ADJUST);
        
        //with +COMPARATOR_REFERENCE_DEGREES on 0 on circle
        //270 degs + 90 degs => 0 degs
        Assert.assertEquals(FlightUtilities.headingCompareTo(270.0, 5.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(270.0, 331.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(270.0, 359.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(270.0, 0.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(270.0, 260.0), FlightUtilities.HEADING_CCW_ADJUST);
        
        //with -COMPARATOR_REFERENCE_DEGREES past 0 on circle
        //60 degs - 90 degs => 330 degs
        Assert.assertEquals(FlightUtilities.headingCompareTo(5.0, 331.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(5.0, 330.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(5.0, 329), FlightUtilities.HEADING_CCW_ADJUST);
        
        //with -COMPARATOR_REFERENCE_DEGREES on 0 on circle
        //90 degs - 90 degs => 0 degs
        Assert.assertEquals(FlightUtilities.headingCompareTo(90.0, 5.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(90.0, 331.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(90.0, 359.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(90.0, 0.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(90.0, 115.0), FlightUtilities.HEADING_CW_ADJUST);
        
        //near 90 degs on circle
        Assert.assertEquals(FlightUtilities.headingCompareTo(80.0, 95.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(80.0, 90.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(90.0, 90.0001), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(90.0, 80.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(90.0, 90.0), FlightUtilities.HEADING_NO_ADJUST);
        
        //near 180 degs on circle
        Assert.assertEquals(FlightUtilities.headingCompareTo(170.0, 185.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(170.0, 180.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(180.0, 180.0001), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(190.0, 180.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(180.0, 180.0), FlightUtilities.HEADING_NO_ADJUST);
        
        //near 270 degs on circle
        Assert.assertEquals(FlightUtilities.headingCompareTo(260.0, 270.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(260.0, 270.0), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(270.0, 270.0001), FlightUtilities.HEADING_CW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(280.0, 270.0), FlightUtilities.HEADING_CCW_ADJUST);
        Assert.assertEquals(FlightUtilities.headingCompareTo(270.0, 270.0), FlightUtilities.HEADING_NO_ADJUST);
    }
}
