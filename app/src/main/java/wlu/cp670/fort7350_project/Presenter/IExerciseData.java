package wlu.cp670.fort7350_project.Presenter;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Map;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;

public interface IExerciseData {

    public interface Presenter{
        boolean checkLastExercise(String name);
        void getLastExerciseData(Exercise exercise);
        void addExerciseSetData(Exercise exercise);

        interface History {
            void getExerciseByFilter(Map<ExerciseFilter, String> filter, String name,
                                     ArrayList<Set> displayList);
        }
        interface Summary{
            int getSummaryData();
        }
    }

    public interface View{
        void displayExerciseData(Exercise exercise, boolean exists);
        void onExerciseSetDataAdded(boolean status);
        void onGetLastExerciseComplete(Exercise exercise);
        void addSet(Set set);
        void updateSet(Set set);
        void deleteSet(Set set);
        interface History{
            void displayHistoryData(Exercise exercise);
            void onHistoryFound(ArrayList<Set> displayList);
        }
        interface Summary{
            void displaySummaryData(Cursor cursor);
        }
    }

}
