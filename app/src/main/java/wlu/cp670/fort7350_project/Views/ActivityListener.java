package wlu.cp670.fort7350_project.Views;

import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.Set;

//listener to detect when item selected
public interface ActivityListener {

    //Data fragment callbacks
    public void onExerciseSetSelected(int position, ArrayList<Set> exerciseSetItems);
    public void onGetLastData(Exercise exercise);
}
