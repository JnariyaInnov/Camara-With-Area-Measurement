package ch.hgdev.toposuite.test.calculation;

import junit.framework.Assert;

import ch.hgdev.toposuite.calculation.Abriss;
import ch.hgdev.toposuite.calculation.Measure;
import ch.hgdev.toposuite.dao.CalculationsDataSource;
import ch.hgdev.toposuite.points.Point;
import ch.hgdev.toposuite.utils.MathUtils;

public class TestAbriss extends CalculationTest {

    public void testRandom() {
        Point p1 = new Point("1", 600.245, 200.729, 0.0, true);
        Point p2 = new Point("2", 623.487, 528.371, 0.0, true);
        Point p3 = new Point("3", 476.331, 534.228, 0.0, true);
        Point p4 = new Point("4", 372.472, 257.326, 0.0, true);

        Abriss a = new Abriss(p1, false);
        a.removeDAO(CalculationsDataSource.getInstance());

        a.getMeasures().add(new Measure(p2, 257.748));
        a.getMeasures().add(new Measure(p3, 254.558));
        a.getMeasures().add(new Measure(p4, 247.655));

        a.compute();

        Assert.assertEquals("328.465", this.df3.format(
                a.getResults().get(0).getDistance()));
        Assert.assertEquals("146.7604", this.df4.format(
                a.getResults().get(0).getUnknownOrientation()));
        Assert.assertEquals("370.2162", this.df4.format(
                a.getResults().get(0).getOrientedDirection()));
        Assert.assertEquals("-365.7077", this.df4.format(
                a.getResults().get(0).getErrAngle() / 10000));
        Assert.assertEquals("-1886.876", this.df3.format(
                a.getResults().get(0).getErrTrans() / 100));

        Assert.assertEquals("355.776", this.df3.format(
                a.getResults().get(1).getDistance()));
        Assert.assertEquals("122.7943", this.df4.format(
                a.getResults().get(1).getUnknownOrientation()));
        Assert.assertEquals("367.0262", this.df4.format(
                a.getResults().get(1).getOrientedDirection()));
        Assert.assertEquals("10.3262", this.df4.format(
                a.getResults().get(1).getErrAngle() / 10000));
        Assert.assertEquals("57.708", this.df3.format(
                a.getResults().get(1).getErrTrans() / 100));

        Assert.assertEquals("234.699", this.df3.format(
                a.getResults().get(2).getDistance()));
        Assert.assertEquals("67.8497", this.df4.format(
                a.getResults().get(2).getUnknownOrientation()));
        Assert.assertEquals("360.1232", this.df4.format(
                a.getResults().get(2).getOrientedDirection()));
        Assert.assertEquals("-44.6184", this.df4.format(
                a.getResults().get(2).getErrAngle() / 10000));
        Assert.assertEquals("-164.492", this.df3.format(
                a.getResults().get(2).getErrTrans() / 100));

        Assert.assertEquals("112.4682", this.df4.format(
                a.getMean()));
        Assert.assertEquals("260.6142", this.df4.format(
                a.getMSE() / 10000));
        Assert.assertEquals("150.4657", this.df4.format(
                a.getMeanErrComp() / 10000));
    }

    public void testRealCase() {
        Point p34 = new Point("34", 556506.667, 172513.91, 620.34, true);
        Point p45 = new Point("45", 556495.16, 172493.912, 623.37, true);
        Point p47 = new Point("47", 556612.21, 172489.274, 0.0, true);
        Abriss a = new Abriss(p34, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p45, 0.0, 91.6892, 23.277, 1.63));
        a.getMeasures().add(new Measure(p47, 281.3521, 100.0471, 108.384, 1.63));
        a.compute();

        // test intermediate values with point 45
        Assert.assertEquals("233.2405",
                this.df4.format(a.getResults().get(0).getUnknownOrientation()));
        Assert.assertEquals("233.2435",
                this.df4.format(a.getResults().get(0).getOrientedDirection()));
        Assert.assertEquals("-0.1", this.df1.format(
                a.getResults().get(0).getErrTrans()));

        // test intermediate values with point 47
        Assert.assertEquals("233.2466",
                this.df4.format(a.getResults().get(1).getUnknownOrientation()));
        Assert.assertEquals("114.5956",
                this.df4.format(a.getResults().get(1).getOrientedDirection()));
        Assert.assertEquals("0.5", this.df1.format(
                a.getResults().get(1).getErrTrans()));

        // test final results
        Assert.assertEquals("233.2435", this.df4.format(a.getMean()));
        Assert.assertEquals("43", this.df0.format(a.getMSE()));
        Assert.assertEquals("30", this.df0.format(a.getMeanErrComp()));
    }

    public void testRealCaseNegative() {
        Point p34 = new Point("34", -43493.333, -27486.090, 620.34, true);
        Point p45 = new Point("45", -43504.840, -27506.088, 623.37, true);
        Point p47 = new Point("47", -43387.790, -27510.726, 0.0, true);
        Abriss a = new Abriss(p34, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p45, 0.0, 91.6892, 23.277, 1.63));
        a.getMeasures().add(new Measure(p47, 281.3521, 100.0471, 108.384, 1.63));
        a.compute();

        // test intermediate values with point 45
        Assert.assertEquals("233.2405",
                this.df4.format(a.getResults().get(0).getUnknownOrientation()));
        Assert.assertEquals("233.2435",
                this.df4.format(a.getResults().get(0).getOrientedDirection()));
        Assert.assertEquals("-30.4", this.df1.format(
                a.getResults().get(0).getErrAngle()));
        Assert.assertEquals("-0.1", this.df1.format(
                a.getResults().get(0).getErrTrans()));

        // test intermediate values with point 47
        Assert.assertEquals("233.2466",
                this.df4.format(a.getResults().get(1).getUnknownOrientation()));
        Assert.assertEquals("114.5956",
                this.df4.format(a.getResults().get(1).getOrientedDirection()));
        Assert.assertEquals("30.4", this.df1.format(
                a.getResults().get(1).getErrAngle()));
        Assert.assertEquals("0.5", this.df1.format(
                a.getResults().get(1).getErrTrans()));

        // test final results
        Assert.assertEquals("233.2435", this.df4.format(a.getMean()));
        Assert.assertEquals("43", this.df0.format(a.getMSE()));
        Assert.assertEquals("30", this.df0.format(a.getMeanErrComp()));
    }

    // See bug #625
    public void testRealCaseAngleCloseToZero() {
        Point p9000 = new Point("9000", 529117.518, 182651.404, 925.059, true);
        Point p9001 = new Point("9001", 529137.864, 182649.391, 919.810, true);
        Point p9002 = new Point("9002", 529112.403, 182631.705, 924.720, true);
        Point p9003 = new Point("9003", 529139.385, 182648.989, MathUtils.IGNORE_DOUBLE, true);
        Point p9004 = new Point("9004", 529112.544, 182632.033, MathUtils.IGNORE_DOUBLE, true);

        Abriss a = new Abriss(p9000, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p9001, 106.3770, 112.4151, 20.890));
        a.getMeasures().add(new Measure(p9002, 216.0699, 97.2887, 20.360));
        a.compute();

        // test intermediate values for point 9001
        Assert.assertEquals("399.9012",
                this.df4.format(a.getResults().get(0).getUnknownOrientation()));
        Assert.assertEquals("106.3792",
                this.df4.format(a.getResults().get(0).getOrientedDirection()));
        Assert.assertEquals("-1010.4",
                this.df1.format(a.getResults().get(0).getErrAngle()));
        Assert.assertEquals("-3.2",
                this.df1.format(a.getResults().get(0).getErrTrans()));

        // test intermediate values for point 9002
        Assert.assertEquals("0.1033",
                this.df4.format(a.getResults().get(1).getUnknownOrientation()));
        Assert.assertEquals("216.0721",
                this.df4.format(a.getResults().get(1).getOrientedDirection()));
        Assert.assertEquals("1010.4",
                this.df1.format(a.getResults().get(1).getErrAngle()));
        Assert.assertEquals("3.2",
                this.df1.format(a.getResults().get(1).getErrTrans()));

        // test final results
        Assert.assertEquals("0.0022", this.df4.format(a.getMean()));

        // test with more measures
        a = new Abriss(p9000, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p9002, 216.0600, 97.2887, 20.360));
        a.getMeasures().add(new Measure(p9003, 107.000, 100.000, 22.00));
        a.getMeasures().add(new Measure(p9001, 106.3770, 112.4151, 20.890));
        a.getMeasures().add(new Measure(p9004, 215.700, 100.00, 20.00));
        a.compute();

        Assert.assertEquals("0.0795", this.df4.format(a.getMean()));

        // test with a different order
        a = new Abriss(p9000, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p9002, 216.0600, 97.2887, 20.360));
        a.getMeasures().add(new Measure(p9003, 107.000, 100.000, 22.00));
        a.getMeasures().add(new Measure(p9004, 215.700, 100.00, 20.00));
        a.getMeasures().add(new Measure(p9001, 106.3770, 112.4151, 20.890));
        a.compute();

        Assert.assertEquals("0.0795", this.df4.format(a.getMean()));

        // another order
        a = new Abriss(p9000, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p9001, 106.3770, 112.4151, 20.890));
        a.getMeasures().add(new Measure(p9002, 216.0600, 97.2887, 20.360));
        a.getMeasures().add(new Measure(p9003, 107.000, 100.000, 22.00));
        a.getMeasures().add(new Measure(p9004, 215.700, 100.00, 20.00));
        a.compute();

        Assert.assertEquals("0.0795", this.df4.format(a.getMean()));
    }

    public void testMeasureDeactivation() {
        Point p34 = new Point("34", 556506.667, 172513.91, 620.34, true);
        Point p45 = new Point("45", 556495.16, 172493.912, 623.37, true);
        Point p47 = new Point("47", 556612.21, 172489.274, 0.0, true);
        Abriss a = new Abriss(p34, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        Measure m1 = new Measure(p45, 0.0, 91.6892, 23.277, 1.63);
        a.getMeasures().add(m1);
        a.getMeasures().add(new Measure(p47, 281.3521, 100.0471, 108.384, 1.63));
        // simulate a deactivation
        a.compute();
        m1.deactivate();
        a.compute();

        // test intermediate values with point 45
        Assert.assertEquals("233.2405",
                this.df4.format(a.getResults().get(0).getUnknownOrientation()));
        Assert.assertEquals("233.2435",
                this.df4.format(a.getResults().get(0).getOrientedDirection()));
        Assert.assertEquals("-0.1", this.df1.format(
                a.getResults().get(0).getErrTrans()));

        // test intermediate values with point 47
        Assert.assertEquals("233.2466",
                this.df4.format(a.getResults().get(1).getUnknownOrientation()));
        Assert.assertEquals("114.5987",
                this.df4.format(a.getResults().get(1).getOrientedDirection()));

        // test final results
        Assert.assertEquals("233.2466", this.df4.format(a.getMean()));
    }

    public void testMeasureDeactivation2() {
        Point p34 = new Point("34", 556506.667, 172513.91, 620.34, true);
        Point p45 = new Point("45", 556495.16, 172493.912, 623.37, true);
        Point p46 = new Point("46", 556517.541, 172491.482, 624.14, true);
        Point p47 = new Point("47", 556612.21, 172489.274, 0.0, true);
        Abriss a = new Abriss(p34, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p45, 0.0, 91.6892, 23.277, 1.63));
        Measure m2 = new Measure(p46, 280.3215, 92.7781, 24.123, 1.63);
        a.getMeasures().add(m2);
        a.getMeasures().add(new Measure(p47, 281.3521, 100.0471, 108.384, 1.63));
        // simulate a deactivation
        a.compute();
        m2.deactivate();
        a.compute();

        // test intermediate values with point 45
        Assert.assertEquals("233.2405",
                this.df4.format(a.getResults().get(0).getUnknownOrientation()));
        Assert.assertEquals("233.2435",
                this.df4.format(a.getResults().get(0).getOrientedDirection()));
        Assert.assertEquals("-0.1", this.df1.format(
                a.getResults().get(0).getErrTrans()));

        // test intermediate values with point 47
        Assert.assertEquals("233.2466",
                this.df4.format(a.getResults().get(2).getUnknownOrientation()));
        Assert.assertEquals("114.5956",
                this.df4.format(a.getResults().get(2).getOrientedDirection()));
        Assert.assertEquals("0.5", this.df1.format(
                a.getResults().get(2).getErrTrans()));

        // test final results
        Assert.assertEquals("233.2435", this.df4.format(a.getMean()));
        Assert.assertEquals("43", this.df0.format(a.getMSE()));
        Assert.assertEquals("30", this.df0.format(a.getMeanErrComp()));
    }

    /**
     * This is a regression test for issue #762.
     * If the user does not provide any distance in the measures, then there cannot be any longitudinal
     * errors.
     *
     * Test values are from NK document, ex 1.1.5.2.
     */
    public void testWithoutDistanceInMeasures() {
        Point p1136 = new Point("1136", 649.0, 780.0, MathUtils.IGNORE_DOUBLE, true);
        Point p1137 = new Point("1137", 615.0, 740.0, MathUtils.IGNORE_DOUBLE, true);
        Point p1138 = new Point("1138", 615.0, 810.0, MathUtils.IGNORE_DOUBLE, true);
        Point p1139 = new Point("1139", 687.0, 804.0, MathUtils.IGNORE_DOUBLE, true);
        Point p1140 = new Point("1140", 676.0, 743.0, MathUtils.IGNORE_DOUBLE, true);

        Abriss a = new Abriss(p1136, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p1137, 0.0, MathUtils.IGNORE_DOUBLE, MathUtils.IGNORE_DOUBLE));
        a.getMeasures().add(new Measure(p1138, 101.218, MathUtils.IGNORE_DOUBLE, MathUtils.IGNORE_DOUBLE));
        a.getMeasures().add(new Measure(p1139, 219.3067, MathUtils.IGNORE_DOUBLE, MathUtils.IGNORE_DOUBLE));
        a.getMeasures().add(new Measure(p1140, 315.113, MathUtils.IGNORE_DOUBLE, MathUtils.IGNORE_DOUBLE));
        a.compute();

        Assert.assertEquals("386", this.df0.format(a.getResults().get(0).getErrAngle()));
        Assert.assertEquals("3.2", this.df1.format(a.getResults().get(0).getErrTrans()));
        Assert.assertTrue(MathUtils.isIgnorable(a.getResults().get(0).getErrLong()));

        Assert.assertEquals("-26", this.df0.format(a.getResults().get(1).getErrAngle()));
        Assert.assertEquals("-0.2", this.df1.format(a.getResults().get(1).getErrTrans()));
        Assert.assertTrue(MathUtils.isIgnorable(a.getResults().get(1).getErrLong()));

        Assert.assertEquals("206", this.df0.format(a.getResults().get(2).getErrAngle()));
        Assert.assertEquals("1.5", this.df1.format(a.getResults().get(2).getErrTrans()));
        Assert.assertTrue(MathUtils.isIgnorable(a.getResults().get(2).getErrLong()));

        Assert.assertEquals("-565", this.df0.format(a.getResults().get(3).getErrAngle()));
        Assert.assertEquals("-4.1", this.df1.format(a.getResults().get(3).getErrTrans()));
        Assert.assertTrue(MathUtils.isIgnorable(a.getResults().get(3).getErrLong()));

        // test final results
        Assert.assertEquals("244.8109", this.df4.format(a.getMean()));
        Assert.assertEquals("413", this.df0.format(a.getMSE()));
        Assert.assertEquals("206", this.df0.format(a.getMeanErrComp()));
    }
}