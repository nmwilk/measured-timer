package test.measuredsoftware.android.timer.model;

import com.measuredsoftware.android.timer.util.CoordTools;
import org.junit.Assert;
import org.junit.Test;

public class AngleCalcTest
{
    @Test
    public void testAngles()
    {
        Assert.assertEquals(0, CoordTools.getAngleFromVelocity(0, -50), 0.0001);
        Assert.assertEquals(45, CoordTools.getAngleFromVelocity(50, -50), 0.0001);
        Assert.assertEquals(90, CoordTools.getAngleFromVelocity(50, 0), 0.0001);
        Assert.assertEquals(135, CoordTools.getAngleFromVelocity(50, 50), 0.0001);
        Assert.assertEquals(180, CoordTools.getAngleFromVelocity(0, 50), 0.0001);
        Assert.assertEquals(225, CoordTools.getAngleFromVelocity(-50, 50), 0.0001);
        Assert.assertEquals(270, CoordTools.getAngleFromVelocity(-50, 0), 0.0001);
        Assert.assertEquals(315, CoordTools.getAngleFromVelocity(-50, -50), 0.0001);
    }
}
