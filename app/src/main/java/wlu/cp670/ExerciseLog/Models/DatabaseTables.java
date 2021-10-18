package wlu.cp670.ExerciseLog.Models;

/**
 * <h1>DatabaseTables</h1>
 * Class that contains the
 * definitions of the database tables used
 *
 *
 * @author Adam Fortier
 * @version 1.0
 * @since 2019-12-10
 */
public class DatabaseTables {

    public class Exercise {
        public static final String TABLE_NAME = "exercise";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TARGET = "target_id";
        public static final String COLUMN_TYPE = "type_id";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + COLUMN_NAME + " TEXT NOT NULL, "
                        + COLUMN_TARGET + " INTEGER NOT NULL, "
                        + COLUMN_TYPE + " INTEGER NOT NULL"
                        + ")";
    }


    public class Sets {

        public static final String TABLE_NAME = "sets";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_EXERCISE = "exercise_id";
        public static final String COLUMN_REPS = "reps";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_ORDER = "order_num";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_EXERCISE + " INTEGER NOT NULL, "
                        + COLUMN_REPS + " INTEGER NOT NULL, "
                        + COLUMN_WEIGHT + " INTEGER NOT NULL, "
                        + COLUMN_ORDER + " INTEGER NOT NULL, "
                        + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")";

    }

    public class Exercise_type {

        public static final String TABLE_NAME = "exercise_type";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TYPE = "type";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TYPE + " TEXT NOT NULL"
                        + ")";
    }

    public class Exercise_target {

        public static final String TABLE_NAME = "exercise_target";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TARGET = "target";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TARGET + " TEXT NOT NULL"
                        + ")";
    }


}
