package test.measuredsoftware.android.timer.model;

import com.measuredsoftware.android.timer.model.RotationModel;
import org.junit.Assert;
import org.junit.Test;

public class RotationModelTest
{
    private RotationModel getTestRotationModel()
    {
        final RotationModel model = new RotationModel();

        model.setPivot(50, 50);
        return model;
    }

    @Test
    public void testSingleTouchNoRotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 75, 75);

        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);

        model.setTouchEnded(0);

        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testSingleTouch90Rotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 75, 50);

        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);

        model.setTouch(0, 50, 75);

        Assert.assertEquals(90, model.getTotalAngle(), 0.0001);

        model.setTouchEnded(0);

        Assert.assertEquals(90, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testSingleTouch180Rotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 75, 50);
        model.setTouch(0, 50, 75);
        model.setTouch(0, 25, 50);

        Assert.assertEquals(180, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testSingleTouch270Rotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 75, 50);
        model.setTouch(0, 50, 75);
        model.setTouch(0, 25, 50);
        model.setTouch(0, 50, 25);

        Assert.assertEquals(270, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testSingleTouchMinus90Rotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 50, 75);

        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);

        model.setTouch(0, 75, 50);

        Assert.assertEquals(-90, model.getTotalAngle(), 0.0001);

        model.setTouchEnded(0);

        Assert.assertEquals(-90, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testSingleTouchMinus180Rotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 25, 50);
        model.setTouch(0, 50, 75);
        model.setTouch(0, 75, 50);

        Assert.assertEquals(-180, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testSingleTouchMinus270Rotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 50, 25);
        model.setTouch(0, 25, 50);
        model.setTouch(0, 50, 75);
        model.setTouch(0, 75, 50);

        Assert.assertEquals(-270, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testSmallMultiTouchRotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 75, 50);
        model.setTouch(1, 50, 75);
        
        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);

        model.setTouch(1, 25, 75);
        Assert.assertEquals(45, model.getTotalAngle(), 0.0001);

        model.setTouch(0, 50, 75);
        Assert.assertEquals(135, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testLargeSingleOneWayRotation()
    {
        final RotationModel model = getTestRotationModel();

        // 9 - > 12
        model.setTouch(0, 10, 50);
        model.setTouch(0, 20, 40);
        model.setTouch(0, 30, 30);
        model.setTouch(0, 40, 20);
        model.setTouch(0, 50, 15);

        Assert.assertEquals(90, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        // 12 -> 3
        model.setTouch(0, 60, 15);
        model.setTouch(0, 70, 20);
        model.setTouch(0, 80, 30);
        model.setTouch(0, 90, 40);
        model.setTouch(0, 90, 50);

        Assert.assertEquals(180, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(180, model.getDisplayAngle(), 0.0001);

        // 3 -> 6
        model.setTouch(0, 90, 60);
        model.setTouch(0, 80, 70);
        model.setTouch(0, 70, 80);
        model.setTouch(0, 60, 85);
        model.setTouch(0, 50, 90);

        Assert.assertEquals(270, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(270, model.getDisplayAngle(), 0.0001);

        // 6 -> 9
        model.setTouch(0, 50, 90);
        model.setTouch(0, 40, 80);
        model.setTouch(0, 30, 70);
        model.setTouch(0, 20, 65);
        model.setTouch(0, 10, 50);

        Assert.assertEquals(360, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(0, model.getDisplayAngle(), 0.0001);

        // 9 - > 12
        model.setTouch(0, 10, 50);
        model.setTouch(0, 20, 40);
        model.setTouch(0, 30, 30);
        model.setTouch(0, 40, 20);
        model.setTouch(0, 50, 15);

        Assert.assertEquals(450, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        // 12 -> 3
        model.setTouch(0, 60, 15);
        model.setTouch(0, 70, 20);
        model.setTouch(0, 80, 30);
        model.setTouch(0, 90, 40);
        model.setTouch(0, 90, 50);

        Assert.assertEquals(540, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(180, model.getDisplayAngle(), 0.0001);
    }

    @Test
    public void testLargeFastSingleOneWayRotation()
    {
        final RotationModel model = getTestRotationModel();

        model.setTouch(0, 0, 50);
        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(0, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 50, 0);
        Assert.assertEquals(90, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 100, 50);
        Assert.assertEquals(180, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(180, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 50, 100);
        Assert.assertEquals(270, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(270, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 0, 50);
        Assert.assertEquals(360, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(0, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 50, 0);
        Assert.assertEquals(450, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 100, 50);
        Assert.assertEquals(540, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(180, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 50, 100);
        Assert.assertEquals(630, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(270, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 0, 50);
        Assert.assertEquals(720, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(0, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 50, 0);
        Assert.assertEquals(810, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 100, 50);
        Assert.assertEquals(900, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(180, model.getDisplayAngle(), 0.0001);
    }

    @Test
    public void testLargeSingleTwoWayRotation()
    {
        final RotationModel model = getTestRotationModel();

        // 9 - > 12
        model.setTouch(0, 10, 50);
        model.setTouch(0, 20, 40);
        model.setTouch(0, 30, 30);
        model.setTouch(0, 40, 20);
        model.setTouch(0, 50, 15);

        Assert.assertEquals(90, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        // 12 -> 3
        model.setTouch(0, 60, 15);
        model.setTouch(0, 70, 20);
        model.setTouch(0, 80, 30);
        model.setTouch(0, 90, 40);
        model.setTouch(0, 90, 50);

        Assert.assertEquals(180, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(180, model.getDisplayAngle(), 0.0001);

        // 3 -> 12
        model.setTouch(0, 90, 50);
        model.setTouch(0, 90, 40);
        model.setTouch(0, 80, 30);
        model.setTouch(0, 70, 20);
        model.setTouch(0, 50, 0);

        Assert.assertEquals(90, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        // 12 -> 3
        model.setTouch(0, 60, 15);
        model.setTouch(0, 70, 20);
        model.setTouch(0, 80, 30);
        model.setTouch(0, 90, 40);
        model.setTouch(0, 90, 50);

        Assert.assertEquals(180, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(180, model.getDisplayAngle(), 0.0001);

        // 3 -> 6
        model.setTouch(0, 90, 60);
        model.setTouch(0, 80, 70);
        model.setTouch(0, 70, 80);
        model.setTouch(0, 60, 85);
        model.setTouch(0, 50, 90);

        Assert.assertEquals(270, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(270, model.getDisplayAngle(), 0.0001);

        // 6 -> 9
        model.setTouch(0, 50, 90);
        model.setTouch(0, 40, 80);
        model.setTouch(0, 30, 70);
        model.setTouch(0, 20, 65);
        model.setTouch(0, 10, 50);

        Assert.assertEquals(360, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(0, model.getDisplayAngle(), 0.0001);

        // 9 - > 12
        model.setTouch(0, 10, 50);
        model.setTouch(0, 20, 40);
        model.setTouch(0, 30, 30);
        model.setTouch(0, 40, 20);
        model.setTouch(0, 50, 15);

        Assert.assertEquals(450, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        // 12 -> 3
        model.setTouch(0, 60, 15);
        model.setTouch(0, 70, 20);
        model.setTouch(0, 80, 30);
        model.setTouch(0, 90, 40);
        model.setTouch(0, 90, 50);

        Assert.assertEquals(540, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(180, model.getDisplayAngle(), 0.0001);

        // 3 - > 12
        model.setTouch(0, 90, 50);
        model.setTouch(0, 80, 40);
        model.setTouch(0, 70, 30);
        model.setTouch(0, 60, 20);
        model.setTouch(0, 50, 15);

        Assert.assertEquals(450, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);
    }

    @Test
    public void testSmallLimitedRotation()
    {
        final RotationModel model = getTestRotationModel();
        model.setMinAngle(-90);
        model.setMaxAngle(90);

        model.setTouch(0, 100, 50);
        model.setTouch(0, 75, 75);
        Assert.assertEquals(45, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(45, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 50, 100);

        Assert.assertEquals(90, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 25, 50);

        Assert.assertEquals(90, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(90, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 25, 75);
        Assert.assertEquals(45, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(45, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 50, 75);
        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(0, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 75, 75);
        Assert.assertEquals(-45, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(315, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 75, 50);
        Assert.assertEquals(-90, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(270, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 75, 25);
        Assert.assertEquals(-90, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(270, model.getDisplayAngle(), 0.0001);

        model.setTouch(0, 75, 50);
        Assert.assertEquals(-45, model.getTotalAngle(), 0.0001);
        Assert.assertEquals(315, model.getDisplayAngle(), 0.0001);
    }

    @Test
    public void testSnapToAngle()
    {
        final RotationModel model = getTestRotationModel();
        model.setSnapTo(12);

        model.setTouch(0, 100, 50);
        model.setTouch(0, 75, 75);

        Assert.assertEquals(48, model.getTotalAngle(), 0.0001);

        model.setTouch(0, 100, 50);
        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);

        model.setTouchEnded(0);

        model.setTouch(0, 100, 50);
        model.setTouch(0, 75, 25);

        Assert.assertEquals(-48, model.getTotalAngle(), 0.0001);
    }

    @Test
    public void testSmallMovements()
    {
        final RotationModel model = getTestRotationModel();

        model.setSnapTo(1);

        model.setTouch(0, 75, 75);
        model.setTouch(0, 74, 75);
        model.setTouch(0, 75, 76);
        model.setTouch(0, 74, 76);
        model.setTouch(0, 74, 75);
        model.setTouch(0, 75, 76);
        model.setTouch(0, 74, 76);
        model.setTouch(0, 74, 75);
        model.setTouch(0, 75, 76);
        model.setTouch(0, 74, 76);
        model.setTouch(0, 74, 75);
        model.setTouch(0, 75, 76);
        model.setTouch(0, 74, 76);
        model.setTouch(0, 74, 75);
        model.setTouch(0, 75, 76);
        model.setTouch(0, 74, 76);
        model.setTouch(0, 74, 75);
        model.setTouch(0, 75, 76);
        model.setTouch(0, 74, 76);
        model.setTouch(0, 75, 75);

        Assert.assertEquals(0, model.getTotalAngle(), 0.0001);
    }
}
