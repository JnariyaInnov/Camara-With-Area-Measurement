package ch.hgdev.toposuite.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

import ch.hgdev.toposuite.App;
import ch.hgdev.toposuite.points.PointsManagerActivity;

/**
 * Useful functions for interacting with View object.
 *
 * @author HGdev
 */
public class ViewUtils {
    /**
     * Color used for deactivated fields
     */
    public static final int DEACTIVATED_COLOR = Color.GRAY;

    /**
     * Regular expression pattern to check if a string can be transformed to a
     * double.
     */
    private static Pattern doublePattern = Pattern.compile("^-?\\d+(\\.\\d*)?$");

    /**
     * Regular expression pattern to check if a string can be transformed to an
     * integer.
     */
    private static Pattern intPattern = Pattern.compile("^-?\\d+$");

    /**
     * Convenient function for easily reading a double value from an EditText.
     *
     * @param editText An EditText object
     * @return The value contained in the edit text as double.
     */
    public static double readDouble(EditText editText) {
        if ((editText != null) && (editText.length() > 0)) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            symbols.setDecimalSeparator('.');

            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(App.locale);
            df.setDecimalFormatSymbols(symbols);

            String input = ViewUtils.readString(editText);

            // +4.1 should be parsed as 4.1 but Double.parseDouble does not handle this case...
            input = input.replace("+", "");

            try {
                // Note: some locale use "," as a separator, hence the hack.
                // To date, I have not found a way to properly parse a double
                // regardless of the locale without causing troubles in
                // some ways. For instance, one can handle a
                // locale aware conversion but some Android keyboard only allow
                // "." as separator when entering numbers... Hence, users can
                // use a locale which uses "," as the decimal separator but
                // we still have to deal with numbers having "." as decimal
                // separator because users cannot input a "," when entering
                // numbers...
                input = df.parse(input.replace(',', '.')).toString();

                if (ViewUtils.doublePattern.matcher(input).matches()) {
                    return df.parse(input).doubleValue();
                }
                // maybe it's using scientific notation? Attempt parsing nonetheless.
                return Double.parseDouble(input);
            } catch (ParseException | NumberFormatException e) {
                Logger.log(Logger.ErrLabel.PARSE_ERROR, e.toString());
                return MathUtils.IGNORE_DOUBLE;
            }
        }
        return MathUtils.IGNORE_DOUBLE;
    }

    /**
     * Convenient function for easily reading a integer value from an EditText.
     *
     * @param editText An EditText object
     * @return The value contained in the edit text as int.
     */
    public static int readInt(EditText editText) {
        if ((editText != null) && (editText.length() > 0)) {
            String input = ViewUtils.readString(editText);
            // +4 should be parsed as 4 but Integer.parseInt does not handle this case...
            input = input.replace("+", "");
            try {
                return (ViewUtils.intPattern.matcher(input).matches()) ?
                        Integer.parseInt(input)
                        : MathUtils.IGNORE_INT;
            } catch (NumberFormatException e) {
                Logger.log(Logger.ErrLabel.PARSE_ERROR, e.toString());
                return MathUtils.IGNORE_INT;
            }
        }
        return MathUtils.IGNORE_INT;
    }

    /**
     * Convenient function for easily reading a string from an EditText.
     *
     * @param editText An EditText object
     * @return The value in the edit text as a string or an empty string if it is empty.
     */
    public static String readString(EditText editText) {
        if ((editText != null) && (editText.length() > 0)) {
            return editText.getText().toString();
        }
        return "";
    }

    /**
     * Check if an EditText is empty or not.
     *
     * @param editText The EditText to check.
     * @return True if empty, false otherwise.
     */
    public static boolean isEmpty(EditText editText) {
        return editText.length() == 0;
    }

    /**
     * Show an error toast.
     *
     * @param context      Calling context.
     * @param errorMessage Error message to display in the toast.
     */
    public static void showToast(Context context, CharSequence errorMessage) {
        Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    /**
     * Start the Points Manager Activity ({@link PointsManagerActivity}).
     *
     * @param currentActivity Activity that performs the redirection.
     */
    public static void redirectToPointsManagerActivity(Activity currentActivity) {
        Intent pointsManagerIntent = new Intent(currentActivity, PointsManagerActivity.class);
        currentActivity.startActivity(pointsManagerIntent);
    }

    /**
     * Convenient function for locking screen orientation.
     *
     * @param currentActivity Activity that request the lock.
     */
    public static void lockScreenOrientation(Activity currentActivity) {
        int currentOrientation = currentActivity.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            currentActivity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            currentActivity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    /**
     * Convenient function for unlocking screen orientation.
     *
     * @param currentActivity Activity that request the unlock.
     */
    public static void unlockScreenOrientation(Activity currentActivity) {
        currentActivity.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }
}
