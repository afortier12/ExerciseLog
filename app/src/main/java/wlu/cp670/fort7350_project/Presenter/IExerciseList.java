package wlu.cp670.fort7350_project.Presenter;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;

public interface IExerciseList {

    public interface Presenter{
        int loadDefaults(Context context);
        void getExerciseList();
        void filterExerciseList(ExerciseFilter filter, String filterValue);
        void updateExerciseList(ArrayList<ExerciseListItem> exerciseList);
        void stop();
    }

    public interface View{
        void updateProgressBar(int value);
        void showProgressBar(int value);
        void displayExerciseList(ArrayList<ExerciseListItem> exerciseList);
        void displayFilteredExerciseList(ArrayList<ExerciseListItem> exerciseList);

    }
}
