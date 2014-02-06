package ch.hgdev.toposuite.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ch.hgdev.toposuite.App;
import ch.hgdev.toposuite.calculation.Calculation;
import ch.hgdev.toposuite.dao.interfaces.DAO;
import ch.hgdev.toposuite.utils.DisplayUtils;
import ch.hgdev.toposuite.utils.Logger;

public class CalculationsDataSource implements DAO {
    private static final String ERROR_CREATE = "Unable to create a new calculation!";
    private static final String ERROR_DELETE = "Unable to delete a calculation!";
    
    private static final String SUCCESS_CREATE = "Calculation successfully created!";
    private static final String SUCCESS_DELETE = "Calculation successfully deleted!";
    
    private static CalculationsDataSource calculationsDataSource;
    
    public static CalculationsDataSource getInstance() {
        if (calculationsDataSource == null) {
            calculationsDataSource = new CalculationsDataSource();
        }
        return calculationsDataSource;
    }
    
    /**
     * 
     * @return
     */
    public ArrayList<Calculation> findAll() {
        SQLiteDatabase db = App.dbHelper.getReadableDatabase();
        
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + CalculationsTable.TABLE_NAME_CALCULATIONS + " ORDER BY id DESC", null);
        ArrayList<Calculation> calculations = new ArrayList<Calculation>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String type = cursor.getString(
                        cursor.getColumnIndex(CalculationsTable.COLUMN_NAME_TYPE));
                String description = cursor.getString(
                        cursor.getColumnIndex(CalculationsTable.COLUMN_NAME_DESCRIPTION));
                String lastModification = cursor.getString(
                        cursor.getColumnIndex(CalculationsTable.COLUMN_NAME_LAST_MODIFICATION));

                calculations.add(new Calculation(type, description, lastModification));
                cursor.moveToNext();
            }
        }

        return calculations;
    }

    /**
     * Create a new Calculation in the database.
     * TODO check for SQL Injection.
     * 
     * @param obj
     *            a calculation
     * @throws SQLiteTopoSuiteException
     */
    @Override
    public void create(Object obj) throws SQLiteTopoSuiteException {
        Calculation calculation = (Calculation) obj;
        SQLiteDatabase db = App.dbHelper.getReadableDatabase();

        ContentValues calculationValues = new ContentValues();
        calculationValues.put(CalculationsTable.COLUMN_NAME_TYPE,
                calculation.getType());
        calculationValues.put(CalculationsTable.COLUMN_NAME_DESCRIPTION,
                calculation.getDescription());
        calculationValues.put(CalculationsTable.COLUMN_NAME_LAST_MODIFICATION,
                DisplayUtils.formatDate(calculation.getLastModification()));

        long rowID = db.insert(CalculationsTable.TABLE_NAME_CALCULATIONS, null, calculationValues);
        if (rowID == -1) {
            Log.e(Logger.TOPOSUITE_SQL_ERROR, ERROR_CREATE + " => " +
                    Logger.formatCalculation(calculation));
            throw new SQLiteTopoSuiteException(ERROR_CREATE);
        }

        Log.i(Logger.TOPOSUITE_SQL_SUCCESS, SUCCESS_CREATE + " => " +
                Logger.formatCalculation(calculation));
    }
    
    @Override
    public void update(Object obj) {
        // TODO
    }

    /**
     * Delete a Calculation.
     * 
     * @param obj
     *            a calculation
     * @throws SQLiteTopoSuiteException
     */
    @Override
    public void delete(Object obj) throws SQLiteTopoSuiteException {
        Calculation calculation = (Calculation) obj;
        SQLiteDatabase db = App.dbHelper.getWritableDatabase();
        
        long rowID = db.delete(CalculationsTable.TABLE_NAME_CALCULATIONS,
                CalculationsTable.COLUMN_NAME_ID + "= 42", null);
        if (rowID == -1) {
            Log.e(Logger.TOPOSUITE_SQL_ERROR, ERROR_DELETE + " => " +
                    Logger.formatCalculation(calculation));
            throw new SQLiteTopoSuiteException(ERROR_DELETE);
        }

        Log.i(Logger.TOPOSUITE_SQL_SUCCESS, SUCCESS_DELETE + " => " +
                Logger.formatCalculation(calculation));
    }
}