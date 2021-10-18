package wlu.cp670.ExerciseLog.Models;


import android.os.Parcel;
import android.os.Parcelable;

/**
* <h1>ExerciseListItem</h1>
* Class that contains the name, type, target
*
*
* @author Adam Fortier
* @version 1.0
* @since 2019-12-10
*/

public class ExerciseListItem implements Parcelable {

    private String name = "";
    private String exerciseTarget = "";   //target muscle group eg) Bicep, Hamstring, ..
    private String exerciseType = ""; //eg) barbell, dumbell, ...

    //Constructor based on existing object
    public ExerciseListItem(ExerciseListItem item) {
        this.name = item.name;
        this.exerciseTarget = item.exerciseTarget;
        this.exerciseType = item.exerciseType;
    }

    //Constructor
    public ExerciseListItem(String name, String exerciseTarget, String exerciseType) {
        this.name = name;
        this.exerciseTarget = exerciseTarget;
        this.exerciseType = exerciseType;
    }

    //parcelable
    protected ExerciseListItem(Parcel in) {
        name = in.readString();
        exerciseTarget = in.readString();
        exerciseType = in.readString();
    }

    //method of parcelable interface
    public static final Creator<ExerciseListItem> CREATOR = new Creator<ExerciseListItem>() {
        @Override
        public ExerciseListItem createFromParcel(Parcel in) {
            return new ExerciseListItem(in);
        }

        @Override
        public ExerciseListItem[] newArray(int size) {
            return new ExerciseListItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(exerciseTarget);
        dest.writeString(exerciseType);
    }
}
