package wlu.cp670.fort7350_project.Presenter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.DatabaseTables;
import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.DatabaseHelper;
import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;
import wlu.cp670.fort7350_project.Views.ExerciseDataFragment;

public class ExerciseData implements IExerciseData.Presenter{

    private final IExerciseData.View view;
    private DatabaseHelper dbHelper;


    private static final String TAG = "ExerciseData";

    public ExerciseData(IExerciseData.View view, Context context) {
        this.view = view;
        dbHelper = new DatabaseHelper(context);
    }

    public void close() {
        dbHelper.close();
    }


    @Override
    public void getLastExerciseData(Exercise exercise) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String query = new StringBuilder().append("SELECT ")
                .append(DatabaseTables.Sets.COLUMN_ORDER).append(", ")
                .append(DatabaseTables.Sets.COLUMN_REPS).append(", ")
                .append(DatabaseTables.Sets.COLUMN_WEIGHT)
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

                Set set = new Set(weight, reps, order_num);
                sets.add(set);
            } while(cursor.moveToNext());

            exercise.addSetList(sets);
        }
    }

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
            if (cursor.getInt(cursor.getColumnIndex("RESULT")) > 0)
                return true;
        }
        return false;
    }

    @Override
    public int addExercise(Exercise exercise) {
        return 0;
    }

    @Override
    public int deleteExercise(Exercise exercise) {
        return 0;
    }

    @Override
    public int getExercisebyFilter(ExerciseFilter filter) {
        return 0;
    }

    @Override
    public int updateExercise(Exercise exercise) {
        return 0;
    }


}
