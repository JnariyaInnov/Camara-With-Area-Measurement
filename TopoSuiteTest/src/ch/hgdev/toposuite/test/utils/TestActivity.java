package ch.hgdev.toposuite.test.utils;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Base class to set generic stuff in order to test an Android activity with
 * Robotium. Every test class testing an Android activity should derive from
 * this one.
 * 
 * @author HGdev
 * 
 * @param <T>
 *            The activity under test.
 */
public class TestActivity<T extends android.app.Activity> extends
        ActivityInstrumentationTestCase2<T> {

    private Solo solo;

    public TestActivity(Class<T> activityClass) {
        super(activityClass);
    }

    public Solo getSolo() {
        return this.solo;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.solo = new Solo(this.getInstrumentation());
    }

    @Override
    protected void tearDown() throws Exception {
        this.solo.finishOpenedActivities();
        super.tearDown();
    }
}