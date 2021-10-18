package wlu.cp670.ExerciseLog.Presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import wlu.cp670.ExerciseLog.Models.ExerciseListItem;
import wlu.cp670.ExerciseLog.Utils.ExerciseFilter;

public interface IExerciseList {

    interface Presenter{
        int loadDefaults(Context context);
        void readExerciseListFromFile();
        void filterExerciseList(Map<ExerciseFilter, String> filter);
        void clearExerciseListFilter();
        void updateExerciseList(ArrayList<ExerciseListItem> exerciseList);
        void stop();
    }

    interface View{
        void updateProgressBar(int count, int total);
        void showProgressBar();
        void displayExerciseList(ArrayList<ExerciseListItem> exerciseList);
        void updateExerciseList(ArrayList<ExerciseListItem> exerciseList);

    }
}
