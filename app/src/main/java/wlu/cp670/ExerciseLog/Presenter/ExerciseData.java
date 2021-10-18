package wlu.cp670.ExerciseLog.Presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import wlu.cp670.ExerciseLog.Models.DatabaseHelper;
import wlu.cp670.ExerciseLog.Models.DatabaseTables;
import wlu.cp670.ExerciseLog.Models.Exercise;
import wlu.cp670.ExerciseLog.Models.Set;
import wlu.cp670.ExerciseLog.Utils.ExerciseFilter;
import wlu.cp670.ExerciseLog.Views.SummaryActivity;

/**
* <h1>ExerciseData</h1>
* ExerciseData implements IExerciseData.Presenter
* linking IExerciseData.View with the data contained in the
* database
*
*
* @author Adam Fortier
* @version 1.0
* @since 2019-12-10
 */

public class ExerciseData implements IExerciseData.Presenter, IExerciseData.Presenter.History,
                    IExerciseData.Presenter.Summary{

    private final IExerciseData.View view;
    private final IExerciseData.View.History view_history;
    private final IExerciseData.View.Summary view_summary;
    private DatabaseHelper dbHelper;

    private static final String TAG = "ExerciseData";

    /**
     * Constructor for IExerciseData.View and IExerciseData.View.History
     * implementations
     *
     * @param view
     * @param history
     * @param context
     */
    public ExerciseData(IExerciseData.View view, IExerciseData.View.History history, Context context) {
        this.view = view;
        this.view_history = history;
        view_summary = null;
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Constructor for IExerciseData.View.Summary implementation
     *
     * @param view
     * @param context
     */
    public ExerciseData(IExerciseData.View.Summary view, Context context) {
        this.view = null;
        this.view_history = null;
        this.view_summary = view;
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Close underlying DatabaseHelper object
     */
    public void dbHelperClose() {
        dbHelper.close();
    }


    /**
     * Implements IExerciseData.Presenter method
     *
     * Called by ExerciseActivity onGetLastData(Exercise) method
     *
     * Gets the last exercise data from the database
     *
     * @param exercise
     */
    @Override
    public void getLastExerciseData(Exercise exercise) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String query = new StringBuilder().append("SELECT ")
                .append(DatabaseTables.Sets.COLUMN_ORDER).append(", ")
                .append(DatabaseTables.Sets.COLUMN_REPS).append(", ")
                .append(DatabaseTables.Sets.COLUMN_WEIGHT).append(", ")
                .append(DatabaseTables.Sets.COLUMN_TIMESTAMP)
                .append(" FROM ").append(DatabaseTables.Exercise.TABLE_NAME)
                .append(" INNER JOIN ").append(DatabaseTables.Sets.TABLE_NAME)
                .append(" ON ").append(DatabaseTables.Sets.TABLE_NAME).append(".")
                .append(DatabaseTables.Sets.COLUMN_EXERCISE).append("=")
                .append(DatabaseTables.Exercise.TABLE_NAME).append(".")
                .append(DatabaseTables.Exercise.COLUMN_ID)
                .append(" WHERE ").append(DatabaseTables.Sets.TABLE_NAME).append(".")
                .append(DatabaseTables.Sets.COLUMN_TIMESTAMP).append("=")
                .append(" (SELECT ").append(DatabaseTables.Sets.TABLE_NAME).append(".")
                .append(DatabaseTables.Sets.COLUMN_TIMESTAMP)
                .append(" FROM ").append(DatabaseTables.Sets.TABLE_NAME)
                .append(" INNER JOIN ").append(DatabaseTables.Exercise.TABLE_NAME)
                .append(" ON ").append(DatabaseTables.Sets.TABLE_NAME).append(".")
                .append(DatabaseTables.Sets.COLUMN_EXERCISE).append(" = ")
                .append(DatabaseTables.Exercise.TABLE_NAME).append(".")
                .append(DatabaseTables.Exercise.COLUMN_ID)
                .append(" WHERE ").append(DatabaseTables.Exercise.COLUMN_NAME).append(" = '")
                .append(exercise.getName()).append("' ORDER BY ")
                .append(DatabaseTables.Sets.COLUMN_TIMESTAMP).append(" DESC LIMIT 1)")
                .append(" ORDER BY ").append(DatabaseTables.Sets.COLUMN_ORDER).append(";")
                .toString();

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()){
            ArrayList<Set> sets = new ArrayList<>();
            do {
                int order_num = cursor.getInt(cursor.getColumnIndex(DatabaseTables.Sets.COLUMN_ORDER));
                int reps = cursor.getInt(cursor.getColumnIndex(DatabaseTables.Sets.COLUMN_REPS));
                int weight = cursor.getInt(cursor.getColumnIndex(DatabaseTables.Sets.COLUMN_WEIGHT));
                String dateTime = cursor.getString(
                        cursor.getColumnIndex(DatabaseTables.Sets.COLUMN_TIMESTAMP));

                Set set = new Set(weight, reps, order_num, dateTime);
                sets.add(set);
            } while(cursor.moveToNext());

            exercise.addSetList(sets);
        }
        cursor.close();

        view.onGetLastExerciseComplete(exercise);

    }

    /**
     * Implements IExerciseData.Presenter method
     *
     * Called by Exercise Activity onExerciseItemSelected(ExerciseListItem)
     *
     * Checks if exercise data exists in the database
     *
     * @param name
     * @return
     */
    @Override
    public boolean checkLastExercise(String name) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String query = new StringBuilder().append("SELECT count(")
                .append(DatabaseTables.Exercise.COLUMN_NAME)
                .append(") AS RESULT FROM ")
                .append(DatabaseTables.Exercise.TABLE_NAME)
                .append(" WHERE ")
                .append(DatabaseTables.Exercise.COLUMN_NAME)
                .append(" = '")
                .append(name)
                .append("';").toString();

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()){
            return cursor.getInt(cursor.getColumnIndex("RESULT")) > 0;
        }
        return false;
    }

    /**
     * Called by addExerciseSetData(Exercise)
     *
     * Checks if the exercise exists in the database
     *
     * @param database
     * @param exercise
     * @return
     */
    protected boolean exerciseExists(SQLiteDatabase database, Exercise exercise){

        int targetId;
        boolean existsFlag = false;

        Cursor cursor = database.query(false, DatabaseTables.Exercise.TABLE_NAME,
                new String[] {DatabaseTables.Exercise.COLUMN_NAME},
                DatabaseTables.Exercise.COLUMN_NAME + " like ? ",
                new String[] {exercise.getName()},
                null, null, null, null);

        if (cursor.moveToFirst())
            existsFlag = true;

        cursor.close();

        return existsFlag;
    }

    /**
     * Called by addExerciseSetData(Exercise)
     *
     * Called by
     * @param database
     * @param exercise
     * @return
     */
    protected int getExerciseId(SQLiteDatabase database, Exercise exercise){

        Cursor cursor = database.query(false, DatabaseTables.Exercise.TABLE_NAME,
                new String[] {DatabaseTables.Exercise.COLUMN_ID},
                DatabaseTables.Exercise.COLUMN_NAME + " like ? ",
                new String[] {exercise.getName()},
                null, null, null, null);

        if (cursor.moveToFirst())
            return cursor.getInt(cursor.getColumnIndex(DatabaseTables.Exercise.COLUMN_ID));

        return 0;
    }

    /**
     * Called by addExerciseSetData(Exercise)
     *
     * @param database
     * @param exercise
     * @return
     */
    protected int getTargetId(SQLiteDatabase database, Exercise exercise){

        Cursor cursor = database.query(false, DatabaseTables.Exercise_target.TABLE_NAME,
                new String[] {DatabaseTables.Exercise_target.COLUMN_ID},
                DatabaseTables.Exercise_target.COLUMN_TARGET + " like ? ",
                new String[] {exercise.getExerciseTarget()},
                null, null, null, null);

        if (cursor.moveToFirst())
            return cursor.getInt(cursor.getColumnIndex(DatabaseTables.Exercise_target.COLUMN_ID));

        return 0;
    }

    /**
     * Called by addExerciseSetData(Exercise)
     *
     * @param database
     * @param exercise
     * @return
     */
    protected int getTypeId(SQLiteDatabase database, Exercise exercise){

        Cursor cursor = database.query(false, DatabaseTables.Exercise_type.TABLE_NAME,
                new String[] {DatabaseTables.Exercise_type.COLUMN_ID},
                DatabaseTables.Exercise_type.COLUMN_TYPE + " like ? ",
                new String[] {exercise.getExerciseType()},
                null, null, null, null);

        if (cursor.moveToFirst())
            return cursor.getInt(cursor.getColumnIndex(DatabaseTables.Exercise_type.COLUMN_ID));

        return 0;
    }

    /**
     * Gets the current date and time as yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Implements IExerciseData.Presenter method
     *
     * Called by ExerciseActivity onSaveExercise(Exercise) method
     *
     * Adds the set data to the database
     *
     * @param exercise
     */
    @Override
    public void addExerciseSetData(Exercise exercise) {
        boolean insertSuccess = false;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (!exerciseExists(database, exercise)){
            contentValues.put(DatabaseTables.Exercise.COLUMN_NAME,
                    exercise.getName());
            contentValues.put(DatabaseTables.Exercise.COLUMN_TARGET,
                    getTargetId(database, exercise));
            contentValues.put(DatabaseTables.Exercise.COLUMN_TYPE,
                    getTypeId(database, exercise));

            if (database.insert(DatabaseTables.Exercise.TABLE_NAME,
                    null, contentValues) > 0)
                insertSuccess = true;
        }

        int exerciseId = getExerciseId(database, exercise);
        if (exerciseId > 0){
            String timestamp = getDateTime();
            for (Set set : exercise.getSetList()) {
                contentValues = new ContentValues();
                contentValues.put(DatabaseTables.Sets.COLUMN_EXERCISE,
                        exerciseId);
                contentValues.put(DatabaseTables.Sets.COLUMN_ORDER,
                        set.getOrderNumber());
                contentValues.put(DatabaseTables.Sets.COLUMN_REPS,
                        set.getReps());
                contentValues.put(DatabaseTables.Sets.COLUMN_WEIGHT,
                        set.getWeight());
                contentValues.put(DatabaseTables.Sets.COLUMN_TIMESTAMP,
                        timestamp);

                if (database.insert(DatabaseTables.Sets.TABLE_NAME,
                        null, contentValues) > 0)
                    insertSuccess = true;
            }

        }
        view.onExerciseSetDataAdded(insertSuccess);
    }

    /**
     * Implements IExerciseData.Presenter.History method
     *
     * Called by ExerciseActivity onSearchHistory() method
     *
     * Adds the set data to the database
     * @param filter
     * @param name
     * @param displayList
     */
    @Override
    public void getExerciseByFilter(Map<ExerciseFilter, String> filter, String name,
                                    ArrayList<Set> displayList, ReadFromDB.Listener listener) {

        String whereClause = "WHERE ";
        boolean startWhere = true;
        boolean allFlag = false;

        if (!name.isEmpty()) {
            whereClause = whereClause + DatabaseTables.Exercise.COLUMN_NAME
                    + " = '" + name + "'";
            startWhere = false;
        }

        for (Map.Entry<ExerciseFilter, String> entry : filter.entrySet()) {
            if (allFlag) break;
            switch (entry.getKey()) {
                case DATE_START:
                    if (startWhere) {
                        whereClause = whereClause + DatabaseTables.Sets.COLUMN_TIMESTAMP
                                + " >= '" + entry.getValue() + "'";
                        startWhere = false;
                    } else {
                        whereClause = whereClause + " AND "
                                + DatabaseTables.Sets.COLUMN_TIMESTAMP
                                + " >= '" + entry.getValue() + "'";
                    }
                    break;
                case DATE_END:
                    if (startWhere) {
                        whereClause = whereClause + DatabaseTables.Sets.COLUMN_TIMESTAMP
                                + " <= '" + entry.getValue() + "'";
                        startWhere = false;
                    } else {
                        whereClause = whereClause + " AND "
                                + DatabaseTables.Sets.COLUMN_TIMESTAMP
                                + " <= '" + entry.getValue() + "'";
                    }
                    break;
                case TARGET:
                    if (startWhere) {
                        whereClause = whereClause + DatabaseTables.Exercise.COLUMN_TARGET
                                + " = '" + entry.getValue() + "'";
                        startWhere = false;
                    } else {
                        whereClause = whereClause + " AND "
                                + DatabaseTables.Exercise.COLUMN_TARGET
                                + " = '" + entry.getValue() + "'";
                    }
                    break;
                case TYPE:
                    if (startWhere) {
                        whereClause = whereClause + DatabaseTables.Exercise.COLUMN_TYPE
                                + " = '" + entry.getValue() + "'";
                        startWhere = false;
                    } else {
                        whereClause = whereClause + " AND "
                                + DatabaseTables.Exercise.COLUMN_TYPE
                                + " = '" + entry.getValue() + "'";
                    }
                case ALL:
                    whereClause = "";
                    allFlag = true;
                    break;
            }
        }

        whereClause = whereClause + ";";
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String query = new StringBuilder().append("SELECT ")
                .append(DatabaseTables.Sets.COLUMN_ORDER).append(", ")
                .append(DatabaseTables.Sets.COLUMN_REPS).append(", ")
                .append(DatabaseTables.Sets.COLUMN_WEIGHT).append(",")
                .append(DatabaseTables.Sets.COLUMN_TIMESTAMP)
                .append(" FROM ").append(DatabaseTables.Exercise.TABLE_NAME)
                .append(" INNER JOIN ").append(DatabaseTables.Sets.TABLE_NAME)
                .append(" ON ").append(DatabaseTables.Sets.TABLE_NAME).append(".")
                .append(DatabaseTables.Sets.COLUMN_EXERCISE).append("=")
                .append(DatabaseTables.Exercise.TABLE_NAME).append(".").append(DatabaseTables.Exercise.COLUMN_ID)
                .append(" ").append(whereClause)
                .toString();

        ReadFromDB readFromDB = new ReadFromDB(view_history, database, dbHelper,
                displayList, listener);
        readFromDB.execute(query);



    }

    protected IExerciseData.View getView() {
        return view;
    }


    protected DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public int getSummaryData() {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String query = new StringBuilder().append("SELECT ")
                .append(DatabaseTables.Exercise.TABLE_NAME).append(".")
                .append(DatabaseTables.Exercise.COLUMN_ID).append(", ")
                .append(SummaryActivity.COLUMN_DATE).append(", ")
                .append(SummaryActivity.COLUMN_TARGET).append(", ")
                .append(SummaryActivity.COLUMN_EXERCISE).append(", MAX(")
                .append(SummaryActivity.COLUMN_WEIGHT).append(") as ")
                .append(SummaryActivity.COLUMN_MAX_WEIGHT).append(", MIN(")
                .append(SummaryActivity.COLUMN_WEIGHT).append(") as ")
                .append(SummaryActivity.COLUMN_MIN_WEIGHT).append(", MAX(")
                .append(SummaryActivity.COLUMN_REPS).append(") as ")
                .append(SummaryActivity.COLUMN_MAX_REPS).append(", MIN(")
                .append(SummaryActivity.COLUMN_REPS).append(") as ")
                .append(SummaryActivity.COLUMN_MIN_REPS)
                .append(" FROM ").append(DatabaseTables.Exercise.TABLE_NAME)
                .append(" INNER JOIN ").append(DatabaseTables.Sets.TABLE_NAME)
                .append(" ON ").append(DatabaseTables.Sets.TABLE_NAME). append(".")
                .append(DatabaseTables.Sets.COLUMN_EXERCISE).append(" = ")
                .append(DatabaseTables.Exercise.TABLE_NAME).append(".")
                .append(DatabaseTables.Exercise.COLUMN_ID)
                .append(" INNER JOIN ").append(DatabaseTables.Exercise_target.TABLE_NAME)
                .append(" ON ").append(DatabaseTables.Exercise_target.TABLE_NAME).append(".")
                .append(DatabaseTables.Exercise_target.COLUMN_ID).append(" = ")
                .append(DatabaseTables.Exercise.TABLE_NAME).append(".")
                .append(DatabaseTables.Exercise.COLUMN_TARGET)
                .append(" GROUP BY date(").append(SummaryActivity.COLUMN_DATE).append("), ")
                .append(SummaryActivity.COLUMN_TARGET).append(", ")
                .append(SummaryActivity.COLUMN_EXERCISE)
                .append(" ORDER BY ").append(DatabaseTables.Sets.COLUMN_TIMESTAMP).append(" DESC LIMIT 5;")
                .toString();

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() > 0)
            view_summary.displaySummaryData(cursor);

        return cursor.getCount();
    }

    /**
     * <h1>ReadFromDB</h1>
     * Extends AsyncTask
     * runs a query from the database and returns an ArrayList of Set objects
     *
     *
     * @author Adam Fortier
     * @version 1.0
     * @since 2019-12-10
     */
    protected static class ReadFromDB extends AsyncTask<String, Integer, ArrayList<Set>> {

        private IExerciseData.View.History view;
        private SQLiteDatabase database;
        private DatabaseHelper dbHelper;
        private Listener listener;
        private ArrayList<Set> displayList;

        public ReadFromDB(IExerciseData.View.History view, SQLiteDatabase database,
                          DatabaseHelper dbHelper, ArrayList<Set> displayList,
                          Listener listener) {
            this.view = view;
            this.database = database;
            this.dbHelper = dbHelper;
            this.displayList = displayList;
            this.listener = listener;
        }

        public interface Listener {
            void onComplete(ArrayList<Set> setList);
        }

        @Override
        protected ArrayList<Set> doInBackground(String... strings) {

            String query = strings[0];
            database = dbHelper.getWritableDatabase();
            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {
                    int order_num = cursor.getInt(cursor.getColumnIndex(DatabaseTables.Sets.COLUMN_ORDER));
                    int reps = cursor.getInt(cursor.getColumnIndex(DatabaseTables.Sets.COLUMN_REPS));
                    int weight = cursor.getInt(cursor.getColumnIndex(DatabaseTables.Sets.COLUMN_WEIGHT));
                    String dateTime = cursor.getString(
                            cursor.getColumnIndex(DatabaseTables.Sets.COLUMN_TIMESTAMP));

                    Set set = new Set(weight, reps, order_num, dateTime);
                    displayList.add(set);
                    SystemClock.sleep(100);
                } while (cursor.moveToNext());

            }
            cursor.close();

            return displayList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            view.showProgress();
        }

        @Override
        protected void onPostExecute(ArrayList<Set> displayList) {
            if (displayList != null) {
                view.onHistoryFound(displayList);
            }
            if (listener != null) listener.onComplete(displayList);
            super.onPostExecute(displayList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }



    }
}
