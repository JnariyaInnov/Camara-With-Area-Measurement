package ch.hgdev.toposuite.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ch.hgdev.toposuite.App;
import ch.hgdev.toposuite.dao.interfaces.DAO;
import ch.hgdev.toposuite.points.Point;
import ch.hgdev.toposuite.utils.Logger;

/**
 * 
 * @author HGdev
 */
public class PointsDataSource implements DAO {
    private static final String ERROR_CREATE = "Unable to create a new point!";
    private static final String ERROR_DELETE = "Unable to delete a point!";
    
    private static final String SUCCESS_CREATE = "Point successfully created!";
    private static final String SUCCESS_DELETE = "Point successfully deleted!";
    
    private static PointsDataSource pointsDataSource;
    
    public static PointsDataSource getInstance() {
        if (pointsDataSource == null) {
            pointsDataSource = new PointsDataSource();
        }
        return pointsDataSource;
    }
    
    /**
     * 
     * @return
     */
    public ArrayList<Point> findAll() {
        SQLiteDatabase db = App.dbHelper.getReadableDatabase();
        
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + PointsTable.TABLE_NAME_POINTS + " ORDER BY number ASC", null);
        ArrayList<Point> points = new ArrayList<Point>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                int number = cursor.getInt(
                        cursor.getColumnIndex(PointsTable.COLUMN_NAME_NUMBER));
                double east = cursor.getDouble(
                        cursor.getColumnIndex(PointsTable.COLUMN_NAME_EAST));
                double north = cursor.getDouble(
                        cursor.getColumnIndex(PointsTable.COLUMN_NAME_NORTH));
                double altitude = cursor.getDouble(cursor.getColumnIndex(PointsTable.COLUMN_NAME_ALTITUDE));
                boolean isBasePoint = cursor.getInt(
                        cursor.getColumnIndex(PointsTable.COLUMN_NAME_BASE_POINT)) == 1;

                points.add(new Point(number, east, north, altitude, isBasePoint));
                cursor.moveToNext();
            }
        }

        return points;
    }

    /**
     * Create a new Point in the database.
     * 
     * @param obj
     *            a point
     * @throws SQLiteTopoSuiteException
     */
    @Override
    public void create(Object obj) throws SQLiteTopoSuiteException {
        Point point = (Point) obj;
        SQLiteDatabase db = App.dbHelper.getReadableDatabase();

        ContentValues pointValues = new ContentValues();
        pointValues.put(PointsTable.COLUMN_NAME_NUMBER, point.getNumber());
        pointValues.put(PointsTable.COLUMN_NAME_EAST, point.getEast());
        pointValues.put(PointsTable.COLUMN_NAME_NORTH, point.getNorth());
        pointValues.put(PointsTable.COLUMN_NAME_ALTITUDE, point.getAltitude());
        pointValues.put(PointsTable.COLUMN_NAME_BASE_POINT, point.isBasePoint() ? 1 : 0);

        long rowID = db.insert(PointsTable.TABLE_NAME_POINTS, null, pointValues);
        if (rowID == -1) {
            Log.e(Logger.TOPOSUITE_SQL_ERROR, ERROR_CREATE + " => " +
                    Logger.formatPoint(point));
            throw new SQLiteTopoSuiteException(ERROR_CREATE);
        }

        Log.i(Logger.TOPOSUITE_SQL_SUCCESS, SUCCESS_CREATE + " => " +
                Logger.formatPoint(point));
    }
    
    @Override
    public void update(Object obj) {
        // TODO
    }

    /**
     * Delete a Point.
     * 
     * @param obj
     *            a point
     * @throws SQLiteTopoSuiteException
     */
    @Override
    public void delete(Object obj) throws SQLiteTopoSuiteException {
        Point point = (Point) obj;
        SQLiteDatabase db = App.dbHelper.getWritableDatabase();
        
        long rowID = db.delete(PointsTable.TABLE_NAME_POINTS,
                PointsTable.COLUMN_NAME_NUMBER + "=" + point.getNumber(), null);
        if (rowID == -1) {
            Log.e(Logger.TOPOSUITE_SQL_ERROR, ERROR_DELETE + " => " +
                    Logger.formatPoint(point));
            throw new SQLiteTopoSuiteException(ERROR_DELETE);
        }

        Log.i(Logger.TOPOSUITE_SQL_SUCCESS, SUCCESS_DELETE + " => " +
                Logger.formatPoint(point));
    }
}