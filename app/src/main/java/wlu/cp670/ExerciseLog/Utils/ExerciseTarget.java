package wlu.cp670.ExerciseLog.Utils;

import androidx.annotation.NonNull;

public enum ExerciseTarget {
    BICEPS, TRICEPS, FOREARMS, WRISTS,
    FRONT_DELTOIDS{
        @NonNull
        @Override
        public String toString() {
            return "FRONT DELTOIDS";
        }
    }, REAR_DELTOIDS{
        @NonNull
        @Override
        public String toString() {
            return "REAR DELTOIDS";
        }
    }, SIDE_DELTOID{
        @NonNull
        @Override
        public String toString() {
            return "SIDE DELTOIDS";
        }
    }, TRAPEZIUS,
    LOWER_BACK{
        @NonNull
        @Override
        public String toString() {
            return "LOWER BACK";
        }
    }, LATS, UPPER_BACK{
        @NonNull
        @Override
        public String toString() {
            return "UPPER BACK";
        }
    },
    HAMSTRING, QUADRICEPS, CALF, GLUTEOUS
}

