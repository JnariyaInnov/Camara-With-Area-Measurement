package ch.hgdev.toposuite.test.calculation;

import android.test.AndroidTestCase;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import ch.hgdev.toposuite.App;
import ch.hgdev.toposuite.test.testutils.Util;

/**
 * Base class for any calculation test.
 *
 * @author HGdev
 *
 */
public class CalculationTest extends AndroidTestCase {
    protected DecimalFormat df1;
    protected DecimalFormat df2;
    protected DecimalFormat df3;
    protected DecimalFormat df4;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.df1 = new DecimalFormat("0.0");
        this.df1.setRoundingMode(RoundingMode.HALF_UP);
        this.df2 = new DecimalFormat("0.00");
        this.df2.setRoundingMode(RoundingMode.HALF_UP);
        this.df3 = new DecimalFormat("0.000");
        this.df3.setRoundingMode(RoundingMode.HALF_UP);
        this.df4 = new DecimalFormat("0.0000");
        this.df4.setRoundingMode(RoundingMode.HALF_UP);

        // we want to keep a good precision for the tests
        App.setCoordinateDecimalRounding(20);

        // make sure we use a consistent locale for these tests
        Util.setLocale("en", "US");
    }
}