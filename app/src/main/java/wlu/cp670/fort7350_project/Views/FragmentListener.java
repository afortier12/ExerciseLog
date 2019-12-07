package wlu.cp670.fort7350_project.Views;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;

//listener to detect when item selected
public interface FragmentListener {

    //Data fragment callbacks
    void onSaveExercise(Exercise exercise);
    void onGetLastData(Exercise exercise);

    //List fragment callbacks
    void onExerciseItemSelected(ExerciseListItem exerciseListItem);
    void onFilterSelected(Map<ExerciseFilter, String> filter);
    void onClearFilter();

    //History fragment callbacks
    void onSearchHistory(String exerciseName, String startDate, String endDate);

}