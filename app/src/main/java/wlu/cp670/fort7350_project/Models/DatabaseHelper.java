package wlu.cp670.fort7350_project.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import wlu.cp670.fort7350_project.Utils.ExerciseTarget;
import wlu.cp670.fort7350_project.Utils.ExerciseType;

//reference
// http://www.informit.com/articles/article.aspx?p=2731932&seqNum=2


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "exercise_log.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //create exercise table
        database.execSQL(DatabaseTables.Exercise.CREATE_TABLE);
        //create exercise type table
        database.execSQL(DatabaseTables.Exercise_type.CREATE_TABLE);
        //create sets table
        database.execSQL(DatabaseTables.Sets.CREATE_TABLE);
        //create target table
        database.execSQL(DatabaseTables.Exercise_target.CREATE_TABLE);


        //copy enum values to the database
        writeDefaultValue(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseTables.Exercise.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseTables.Sets.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseTables.Exercise_type.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseTables.Exercise_target.TABLE_NAME);
        onCreate(database);
    }

    private void writeDefaultValue(SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();

        //insert ExerciseTarget enum values into exercise_target table
        database.beginTransaction();
        try {
            for (ExerciseTarget target : ExerciseTarget.values()) {
                contentValues.put(DatabaseTables.Exercise_target.COLUMN_TARGET, target.name());
                long newRowId = database.insert(DatabaseTables.Exercise_target.TABLE_NAME, null, contentValues);
                contentValues.clear();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        //insert ExerciseType enum values into exercise_target table
        database.beginTransaction();
        try{
            for(ExerciseType type: ExerciseType.values()){
                contentValues.put(DatabaseTables.Exercise_type.COLUMN_TYPE,type.name());
            long newRowId = database.insert(DatabaseTables.Exercise_type.TABLE_NAME, null, contentValues);
            contentValues.clear();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

}
