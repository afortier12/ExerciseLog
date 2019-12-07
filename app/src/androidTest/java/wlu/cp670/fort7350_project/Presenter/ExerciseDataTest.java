package wlu.cp670.fort7350_project.Presenter;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wlu.cp670.fort7350_project.Models.DatabaseHelper;
import wlu.cp670.fort7350_project.Models.DatabaseTables;
import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;
import wlu.cp670.fort7350_project.Utils.ExerciseTarget;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ExerciseDataTest {

    ExerciseData exerciseData;
    Context targetContext;
    IExerciseData.View view;
    IExerciseData.View.History view_history;
    SQLiteDatabase db;

    @Before
    public void setUp() throws Exception {

        targetContext = getInstrumentation().getTargetContext();
        view = new IExerciseData.View() {
            @Override
            public void displayExerciseData(Exercise exercise, boolean exists) {

            }

            @Override
            public void onExerciseSetDataAdded(boolean status) {

            }

            @Override
            public void onGetLastExerciseComplete(Exercise exercise) {

            }

            @Override
            public void addSet(Set set) {

            }

            @Override
                public void updateSet(Set set) {

            }

            @Override
            public void deleteSet(Set set) {

            }
        };

        view_history = new IExerciseData.View.History() {
            @Override
            public void onHistoryFound(ArrayList<Set> displayList) {

            }
        };

        exerciseData = new ExerciseData(view, view_history, targetContext);

        db = exerciseData.getDbHelper().getWritableDatabase();

        String sqlStatement = "INSERT INTO " + DatabaseTables.Exercise.TABLE_NAME
                + " (" + DatabaseTables.Exercise.COLUMN_NAME
                + ", " + DatabaseTables.Exercise.COLUMN_TARGET
                + ", " + DatabaseTables.Exercise.COLUMN_TYPE
                + ") VALUES ('Test', 1, 1);";
        db.execSQL(sqlStatement);

        sqlStatement = "INSERT INTO " + DatabaseTables.Sets.TABLE_NAME
                + "(" + DatabaseTables.Sets.COLUMN_EXERCISE + ", "
                + DatabaseTables.Sets.COLUMN_REPS + ", "
                + DatabaseTables.Sets.COLUMN_WEIGHT + ", "
                + DatabaseTables.Sets.COLUMN_ORDER + ", "
                + DatabaseTables.Sets.COLUMN_TIMESTAMP + ") "
                + "SELECT " + DatabaseTables.Exercise.COLUMN_ID
                + ", 10, 10, 1, CURRENT_TIMESTAMP "
                + "FROM " + DatabaseTables.Exercise.TABLE_NAME
                + " WHERE " + DatabaseTables.Exercise.COLUMN_NAME
                + " = 'Test';";
        db.execSQL(sqlStatement);
        db.close();

    }

    @After
    public void tearDown() throws Exception {
        db = exerciseData.getDbHelper().getWritableDatabase();
        String sqlStatement = "DELETE FROM " + DatabaseTables.Sets.TABLE_NAME
                + " WHERE " + DatabaseTables.Sets.COLUMN_EXERCISE + " = (SELECT "
                + DatabaseTables.Exercise.COLUMN_ID + " FROM "
                + DatabaseTables.Exercise.TABLE_NAME + " WHERE "
                + DatabaseTables.Exercise.COLUMN_NAME + " = 'Test')";
        db.execSQL(sqlStatement);

        sqlStatement = "DELETE FROM " + DatabaseTables.Exercise.TABLE_NAME
                + " WHERE " + DatabaseTables.Exercise.COLUMN_NAME + " = 'Test';";
        db.execSQL(sqlStatement);
        db.close();
    }

    @Test
    public void constructorTest(){

        assertNotNull(exerciseData.getDbHelper());
        assertThat(exerciseData.getView(), instanceOf(IExerciseData.View.class));
    }

    @Test
    public void checkLastExercise_exerciseExists(){

        assertTrue(exerciseData.checkLastExercise("Test"));
        exerciseData.close();

    }

    @Test
    public void checkLastExercise_exerciseDoesNotExist(){

        assertFalse(exerciseData.checkLastExercise("TestFail"));
        exerciseData.close();
    }

    @Test
    public void getLastExerciseData_exerciseFound(){

        Exercise exercise = new Exercise("Test", "FREEWEIGHT", "BICEP");
        exerciseData.getLastExerciseData(exercise);
        exerciseData.close();
        assertNotNull(exercise.getSetList());
        assertEquals(exercise.getSetList().size(), 1);
    }

    @Test
    public void getLastExerciseData_exerciseNotFound(){
        Exercise exercise = new Exercise("TestFail", "BARBELL", "BICEP");
        exerciseData.getLastExerciseData(exercise);
        exerciseData.close();
        assertNotNull(exercise.getSetList());
        assertEquals(exercise.getSetList().size(), 0);
    }

    @Test
    public void getExerciseByFilter_dateRangeFound(){
        Map<ExerciseFilter, String> filterMap = new HashMap<>();

        filterMap.put(ExerciseFilter.DATE_START, "2019-11-01 11:59:59");
        filterMap.put(ExerciseFilter.DATE_END, "2020-01-01 11:59:59");

        ArrayList<Set> displayList = new ArrayList<>();
        exerciseData.getExerciseByFilter(filterMap, "Test", displayList);
        assertEquals(displayList.size(), 1);
    }

    @Test
    public void getExerciseByFilter_dateRangeNotFound(){
        Map<ExerciseFilter, String> filterMap = new HashMap<>();

        filterMap.put(ExerciseFilter.DATE_START, "2019-11-01 11:59:59");
        filterMap.put(ExerciseFilter.DATE_END, "2020-01-01 11:59:59");

        ArrayList<Set> displayList = new ArrayList<>();
        exerciseData.getExerciseByFilter(filterMap, "Test", displayList);
        assertEquals(displayList.size(), 1);
    }

    @Test
    public void getExerciseByFilter_targetFound(){

    }

    @Test
    public void getExerciseByFilter_targetNotFound(){

    }

    @Test
    public void getExerciseByFilter_typeFound(){

    }

    @Test
    public void getExerciseByFilter_typeNotFound(){

    }

    @Test
    public void exerciseExists_Found(){

        db = exerciseData.getDbHelper().getWritableDatabase();
        Exercise exercise = new Exercise("Test", "BARBELL", "BICEP");

        assertTrue(exerciseData.exerciseExists(db, exercise));
        db.close();
    }

    @Test
    public void exerciseExists_NotFound(){
        db = exerciseData.getDbHelper().getWritableDatabase();
        Exercise exercise = new Exercise("TestFail", "BARBELL", "BICEP");

        assertFalse(exerciseData.exerciseExists(db, exercise));
        db.close();
    }

    @Test
    public void getExerciseId_Found(){
        db = exerciseData.getDbHelper().getWritableDatabase();
        Exercise exercise = new Exercise("Test", "BARBELL", "BICEP");

        assertThat(exerciseData.getExerciseId(db, exercise), greaterThan(0));
        db.close();
    }

    @Test
    public void getExerciseId_NotFound(){
        db = exerciseData.getDbHelper().getWritableDatabase();
        Exercise exercise = new Exercise("TestFail", "BARBELL", "BICEP");

        assertEquals(exerciseData.getExerciseId(db, exercise), 0);
        db.close();
    }

    @Test
    public void getTargetId_Found(){
        db = exerciseData.getDbHelper().getWritableDatabase();
        Exercise exercise = new Exercise("Test", "BARBELL", "BICEP");

        assertThat(exerciseData.getTargetId(db, exercise), greaterThan(0));
        db.close();
    }

    @Test
    public void getTargetId_NotFound(){
        db = exerciseData.getDbHelper().getWritableDatabase();
        Exercise exercise = new Exercise("Test", "BARBELL", "FAIL");

        assertEquals(exerciseData.getTargetId(db, exercise), 0);
        db.close();
    }


    @Test
    public void getTypeId_Found(){
        db = exerciseData.getDbHelper().getWritableDatabase();
        Exercise exercise = new Exercise("Test", "FREEWEIGHT", "BICEP");

        assertThat(exerciseData.getTypeId(db, exercise), greaterThan(0));
        db.close();
    }

    @Test
    public void getTypeId_NotFound(){
        db = exerciseData.getDbHelper().getWritableDatabase();
        Exercise exercise = new Exercise("Test", "FAIL", "BICEP");

        assertEquals(exerciseData.getTypeId(db, exercise), 0);
        db.close();
    }

    @Test
    public void addExercise(){
        Exercise exercise = new Exercise("Test", "BARBELL", "BICEP");
        exercise.addSet(new Set(10,10,1));
        exercise.addSet(new Set(10,10,2));
        exercise.addSet(new Set(15,8,3));

        db = exerciseData.getDbHelper().getWritableDatabase();
        String sqlStatement = "DELETE FROM " + DatabaseTables.Sets.TABLE_NAME
                + " WHERE " + DatabaseTables.Sets.COLUMN_EXERCISE + " = (SELECT "
                + DatabaseTables.Exercise.COLUMN_ID + " FROM "
                + DatabaseTables.Exercise.TABLE_NAME + " WHERE "
                + DatabaseTables.Exercise.COLUMN_NAME + " = 'Test')";
        db.execSQL(sqlStatement);
        db.close();

        exerciseData.addExerciseSetData(exercise);

        Map<ExerciseFilter, String> filterMap = new HashMap<>();
        filterMap.put(ExerciseFilter.ALL, "");

        ArrayList<Set> displayList = new ArrayList<>();
        exerciseData.getExerciseByFilter(filterMap, "Test", displayList);
        assertEquals(displayList.size(), 3);

        assertEquals(displayList.get(0).getOrderNumber(), 1);
        assertEquals(displayList.get(1).getOrderNumber(), 2);
        assertEquals(displayList.get(2).getOrderNumber(), 3);
        assertEquals(displayList.get(0).getReps(), 10);
        assertEquals(displayList.get(1).getReps(), 10);
        assertEquals(displayList.get(2).getReps(), 8);
        assertThat(displayList.get(0).getWeight(), equalTo(10.0));
        assertThat(displayList.get(1).getWeight(), equalTo(10.0));
        assertThat(displayList.get(2).getWeight(), equalTo(15.0));


    }

    @Test
    public void deleteExercise(){
        Exercise exercise = new Exercise("Test", "BARBELL", "BICEP");
    }

}
