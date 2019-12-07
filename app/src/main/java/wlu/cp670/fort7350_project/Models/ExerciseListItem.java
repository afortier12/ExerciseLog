package wlu.cp670.fort7350_project.Models;


public class ExerciseListItem {

    private String name = "";
    private String exerciseTarget = "";   //target muscle group eg) Bicep, Hamstring, ..
    private String exerciseType = ""; //eg) barbell, dumbell, ...

    public ExerciseListItem(ExerciseListItem item) {
        this.name = item.name;
        this.exerciseTarget = item.exerciseTarget;
        this.exerciseType = item.exerciseType;
    }

    public ExerciseListItem(String name, String exerciseTarget, String exerciseType) {
        this.name = name;
        this.exerciseTarget = exerciseTarget;
        this.exerciseType = exerciseType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExerciseTarget() {
        return exerciseTarget;
    }

    public void setExerciseTarget(String exerciseTarget) {
        this.exerciseTarget = exerciseTarget;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    @Override
    public String toString() {
        return "ExerciseListItem{" +
                "name='" + name + '\'' +
                ", exerciseTarget='" + exerciseTarget + '\'' +
                ", exerciseType='" + exerciseType + '\'' +
                '}';
    }

}
