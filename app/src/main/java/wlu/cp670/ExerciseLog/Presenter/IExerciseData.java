package wlu.cp670.ExerciseLog.Presenter;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Map;

import wlu.cp670.ExerciseLog.Models.Exercise;
import wlu.cp670.ExerciseLog.Models.Set;
import wlu.cp670.ExerciseLog.Utils.ExerciseFilter;

public interface IExerciseData {

    interface Presenter{
        boolean checkLastExercise(String name);
        void getLastExerciseData(Exercise exercise);
        void addExerciseSetData(Exercise exercise);

        interface History {
            void getExerciseByFilter(Map<ExerciseFilter, String> filter, String name,
                                     ArrayList<Set> displayList,
                                     ExerciseData.ReadFromDB.Listener listener);
        }
        interface Summary{
            int getSummaryData();
        }
    }

    interface View{
        void displayExerciseData(Exercise exercise, boolean exists);
        void onExerciseSetDataAdded(boolean status);
        void onGetLastExerciseComplete(Exercise exercise);
        void addSet(Set set);
        void updateSet(Set set);
        void deleteSet(Set set);
        interface History{
            void displayHistoryData(Exercise exercise);
            void onHistoryFound(ArrayList<Set> displayList);
            void showProgress();
        }
        interface Summary{
            void displaySummaryData(Cursor cursor);
        }
    }

}
