package wlu.cp670.fort7350_project.Presenter;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;

public interface IExerciseData {

    public interface Presenter{
        boolean checkLastExercise(String name);
        void getLastExerciseData(Exercise exercise);
        int addExercise(Exercise exercise);
        int deleteExercise(Exercise exercise);
        int getExercisebyFilter(ExerciseFilter filter);
        int updateExercise(Exercise exercise);
    }

    public interface View{
        void displayExerciseData(Exercise exercise, boolean exists);
        int addSet(Set set);
        int updateSet(Set set);
        int deleteSet(Set set);
    }
}
