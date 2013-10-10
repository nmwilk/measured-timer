package test.measuredsoftware.android.timer.model;

import com.measuredsoftware.android.timer.model.RotationModel;
import org.junit.Assert;
import org.junit.Test;

public class AngleCalcTest
{
    @Test
    public void testAngles()
    {
        Assert.assertEquals(0, RotationModel.getAngleFromVelocity(0, -50), 0.0001);
        Assert.assertEquals(45, RotationModel.getAngleFromVelocity(50, -50), 0.0001);
        Assert.assertEquals(90, RotationModel.getAngleFromVelocity(50, 0), 0.0001);
        Assert.assertEquals(135, RotationModel.getAngleFromVelocity(50, 50), 0.0001);
        Assert.assertEquals(180, RotationModel.getAngleFromVelocity(0, 50), 0.0001);
        Assert.assertEquals(225, RotationModel.getAngleFromVelocity(-50, 50), 0.0001);
        Assert.assertEquals(270, RotationModel.getAngleFromVelocity(-50, 0), 0.0001);
        Assert.assertEquals(315, RotationModel.getAngleFromVelocity(-50, -50), 0.0001);
    }
}
