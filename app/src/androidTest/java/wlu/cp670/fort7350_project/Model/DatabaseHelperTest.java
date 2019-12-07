package wlu.cp670.fort7350_project.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wlu.cp670.fort7350_project.Models.DatabaseHelper;
import wlu.cp670.fort7350_project.Models.DatabaseTables;
import wlu.cp670.fort7350_project.Utils.ExerciseTarget;
import wlu.cp670.fort7350_project.Utils.ExerciseType;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    DatabaseHelper dbHelper;
    Context targetContext;

    @Before
    public void setUp() throws Exception {
        targetContext = getInstrumentation().getTargetContext();
        dbHelper = new DatabaseHelper(targetContext);

    }

    @Test
    public void constructorTest(){
        assertEquals(dbHelper.getDatabaseName(), "exercise_log.db");
    }

    @Test
    public void onCreate_checkTablesCreated(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        assertTrue(db.isOpen());
        String query = "SELECT name FROM sqlite_master WHERE type='table' "
                + "AND name in ('" + DatabaseTables.Exercise.TABLE_NAME + "', '"
                + DatabaseTables.Exercise_target.TABLE_NAME + "', '"
                + DatabaseTables.Exercise_type.TABLE_NAME + "', '"
                + DatabaseTables.Sets.TABLE_NAME
                + "');";
        Cursor cursor = db.rawQuery(query, null);

        assertEquals(cursor.getCount(), 4);

        db.close();

    }

    @Test
    public void writeDefaultValues_exerciseTargets(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT " + DatabaseTables.Exercise_target.COLUMN_TARGET
                + " FROM " + DatabaseTables.Exercise_target.TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(query, null);

        List<String> enumValues = new ArrayList<String>();
        for (ExerciseTarget value: ExerciseTarget.values())
            enumValues.add(value.toString());

        List<String> dbValues = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            dbValues.add(cursor.getString(0));
            cursor.moveToNext();
        }

            assertEquals(dbValues, enumValues);
        db.close();
    }

    @Test
    public void writeDefaultValues_exerciseType(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT " + DatabaseTables.Exercise_type.COLUMN_TYPE
                + " FROM " + DatabaseTables.Exercise_type.TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(query, null);

        List<String> enumValues = new ArrayList<String>();
        for (ExerciseType value: ExerciseType.values())
            enumValues.add(value.toString());

        List<String> dbValues = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            dbValues.add(cursor.getString(0));
            cursor.moveToNext();
        }

        assertEquals(dbValues, enumValues);
        db.close();
    }
}
