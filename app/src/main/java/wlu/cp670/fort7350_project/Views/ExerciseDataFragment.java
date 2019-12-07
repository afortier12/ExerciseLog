package wlu.cp670.fort7350_project.Views;

        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.graphics.Color;
        import android.graphics.PorterDuff;
        import android.graphics.PorterDuffColorFilter;
        import android.graphics.drawable.GradientDrawable;
        import android.os.Bundle;
        import android.text.Editable;
        import android.text.InputFilter;
        import android.text.Spanned;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.constraintlayout.widget.ConstraintLayout;
        import androidx.core.content.ContextCompat;
        import androidx.core.graphics.drawable.DrawableCompat;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;

        import com.google.android.material.floatingactionbutton.FloatingActionButton;

        import java.util.HashMap;
        import java.util.Map;

        import wlu.cp670.fort7350_project.Models.Exercise;
        import wlu.cp670.fort7350_project.Models.Set;
        import wlu.cp670.fort7350_project.R;


/*
    references
        https://medium.com/@CodyEngel/4-ways-to-implement-onclicklistener-on-android-9b956cbd2928
        https://stackoverflow.com/questions/18352324/how-can-can-i-add-custom-buttons-into-an-alertdialogs-layout
 */

public class ExerciseDataFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ExerciseDataFragment";
    private static final String DETAIL_ITEM_ARG = "DETAIL_ITEM";
    private static final String DETAIL_EXISTS_ARG = "DETAIL_EXITS";
    private static final double maxWeight = 1000;
    private static final double minWeight = 2.5;
    private static final double weightMultiple = 2.5;
    private static final int maxReps = 100;
    private static final int minReps = 1;
    private static final int repsMultiple = 1;

    private Exercise exercise;
    private boolean exercise_exists;

    private ExerciseSetArrayAdapter exerciseSetArrayAdapter;
    private FragmentListener listener;

    private TextView title;
    private TextView targetValue;
    private TextView typeValue;
    private EditText weightValue;
    private EditText repsValue;
    private ImageButton weightButton;
    private ImageButton repsIncreaseButton;
    private ImageButton repsDecreaseButton;
    private Button saveSetButton;
    private ProgressBarDialog progressBarDialog;
    private ListView listView;
    private FloatingActionButton saveExerciseButton;

    private final static  Map<Integer, String>  weightIds;
        static {
            weightIds = new HashMap<>();
            weightIds.put(R.id.weight1, "2.5");
            weightIds.put(R.id.weight2, "5");
            weightIds.put(R.id.weight3, "10");
            weightIds.put(R.id.weight4, "25");
            weightIds.put(R.id.weight5, "35");
        }


    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "In onAttach()");
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onCreate()");
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Get back arguments
            if (getArguments() != null) {
                exercise = getArguments().getParcelable(DETAIL_ITEM_ARG);
                exercise_exists = getArguments().getBoolean(DETAIL_EXISTS_ARG);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onCreateView()");

        View view = inflater.inflate(R.layout.fragment_exercise_detail, container, false);

        listView = view.findViewById(R.id.setList);
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {R.color.colorBody, R.color.colorBody}));
        listView.setDividerHeight(1);
        LayoutInflater headerInflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) headerInflater.inflate(R.layout.set_row_header, listView, false);
        listView.addHeaderView(header);

        exerciseSetArrayAdapter = new ExerciseSetArrayAdapter(getActivity(), exercise.getSetList(), this);
        listView.setAdapter(exerciseSetArrayAdapter);

        if (exercise != null) {
            title = view.findViewById(R.id.title);
            typeValue = view.findViewById(R.id.typeValue);
            targetValue = view.findViewById(R.id.targetValue);

            title.setText(exercise.getName());
            typeValue.setText(exercise.getExerciseType());
            targetValue.setText(exercise.getExerciseTarget());
        }

        weightButton = view.findViewById(R.id.weightAddButton);
        weightButton.setOnClickListener(this);

        repsIncreaseButton = view.findViewById(R.id.repIncrButton);
        repsIncreaseButton.setOnClickListener(this);

        repsDecreaseButton = view.findViewById(R.id.repDecrButton);
        repsDecreaseButton.setOnClickListener(this);

        repsValue = view.findViewById(R.id.repsEditText);
        repsValue.addTextChangedListener(new ValueChangeWatcher(maxReps,minReps,repsMultiple));

        weightValue = view.findViewById(R.id.weightEditText);
        weightValue.addTextChangedListener(new ValueChangeWatcher(maxWeight, minWeight,
                weightMultiple));

        saveSetButton = view.findViewById(R.id.save_set_button);
        saveSetButton.setOnClickListener(this);
        setButtonDisabledColorFilter(saveSetButton, true);

        saveExerciseButton = view.findViewById(R.id.save_exercise_button);
        saveExerciseButton.setOnClickListener(this);
        saveExerciseButton.hide();

        progressBarDialog = new ProgressBarDialog(getContext());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onViewCreated()");
        if (exercise_exists){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.exercise_exists)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressBarDialog.show();
                        //callback to ExerciseActivity to get last data
                        listener.onGetLastData(exercise);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Cancel goes here
                    }
                })
                .create()
                .show();


        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        Log.d(TAG, "In onAttachFragment()");
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "In onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "In onResume()");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "In onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "In onPause()");
        progressBarDialog.dismiss();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "In onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "In onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "In onDetach()");
        super.onDetach();
    }

    private void displayWeightDialog(View view) {

        int  displayMetrics = (int)getContext().getResources().getDisplayMetrics().density;
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        final View weightDialog = layoutInflater.inflate(R.layout.weight_dialog, null);
        weightDialog.setLayoutParams(view.getLayoutParams());

        for (Map.Entry<Integer, String> ids : weightIds.entrySet()) {
            RelativeLayout rl = weightDialog.findViewById(ids.getKey());
            ImageButton incButton = rl.findViewById(R.id.inc_button);
            incButton.setOnClickListener(new WeightDialogButtonClick());
            ImageButton decButton = rl.findViewById(R.id.dec_button);
            decButton.setOnClickListener(new WeightDialogButtonClick());
            TextView label = rl.findViewById(R.id.weight_label);
            label.setText(ids.getValue()+"LBS");
            TextView result =rl.findViewById(R.id.weight_result);
            result.setText("0");
        }

        TextView dialogTitle = new TextView(this.getContext());
        dialogTitle.setText(R.string.weight_dialog_title);
        dialogTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
        dialogTitle.setTextSize(20F);
        int pad  = 30 * displayMetrics;
        dialogTitle.setPadding(pad, pad, pad, pad);

        AlertDialog.Builder builderCustom = new AlertDialog.Builder(this.getContext());
        AlertDialog alertDialog =  builderCustom.setView(weightDialog)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ConstraintLayout layout = (ConstraintLayout) view.getParent();
                        EditText weightEdit = layout.findViewById(R.id.weightEditText);
                        TextView total = weightDialog.findViewById(R.id.total_value);
                        weightEdit.setText(total.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .create();

        alertDialog.setCustomTitle(dialogTitle);
        alertDialog.show();

    }

    private void calculateTotalWeight(LinearLayout rDialog){
        double total = 0.0;

        //loop through each weight to calculate toatl weigh selected
        for (Map.Entry<Integer, String> ids : weightIds.entrySet()) {
            RelativeLayout rl = rDialog.findViewById(ids.getKey());
            TextView result =rl.findViewById(R.id.weight_result);
            total = total + Double.valueOf(result.getText().toString()) * Double.valueOf(ids.getValue());
        }

        TextView textView = rDialog.findViewById(R.id.total_value);
        textView.setText(String.valueOf(total));
    }

    /**
     * @param button
     * @param setFlag
     */
    private void setButtonDisabledColorFilter(Button button, boolean setFlag){
        if(setFlag){
            //https://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
            button.getBackground().setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN));
            button.setTextColor(Color.LTGRAY);
            DrawableCompat.setTint(button.getCompoundDrawablesRelative()[0],
                    ContextCompat.getColor(getContext(), R.color.disable_color));
        } else {
            button.getBackground().setColorFilter(null);
            button.setTextColor(getResources().getColor(R.color.button_text));
            DrawableCompat.setTintList(button.getCompoundDrawablesRelative()[0],
                    null);
        }
    }


    private boolean checkWeightField(){

        String weightEntry = weightValue.getText().toString();
        if (weightEntry.isEmpty()) weightEntry = "0";
        Double weightValue = Double.valueOf(weightEntry);
        if ((weightValue.compareTo(maxWeight) > 0) || (weightValue.compareTo((minWeight)) < 0)
                || ((weightValue % weightMultiple) != 0))
            return false;
        else
            return true;

    }

    private boolean checkRepsField(){

        String repsEntry = repsValue.getText().toString();
        if (repsEntry.isEmpty()) repsEntry = "0";
        Integer repsValue  = Integer.valueOf(repsEntry);
        if ((repsValue.compareTo(maxReps) > 0) || (repsValue.compareTo(minReps) < 0)
                || ((repsValue % repsMultiple) != 0))
            return false;
        else
            return true;

    }

    @Override
    public void onClick(View v) {
        int value;
        //boolean valChangeFlag = false;
        switch (v.getId()) {
            case R.id.weightAddButton:
                displayWeightDialog(v);
            //    valChangeFlag = true;
                break;
            case R.id.repIncrButton:
                if (repsValue.getText().length() == 0) value = 0;
                else value = Integer.valueOf(repsValue.getText().toString());

                if (++value > 100) value =100;
                repsValue.setText(String.valueOf(value));

                break;
            case R.id.repDecrButton:
                if (repsValue.getText().length() == 0) value = 0;
                else value = Integer.valueOf(repsValue.getText().toString());
                if (--value < 1) value =1;
                repsValue.setText(String.valueOf(value));

                break;

            case R.id.save_set_button:
                boolean saveOkFlag = true;
                if (!checkWeightField()){
                    weightValue.setError("Error");
                    saveOkFlag = false;
                }
                if (!checkRepsField()){
                    repsValue.setError("Error");
                    saveOkFlag = false;
                }
                if (saveOkFlag) {
                    exercise.addSet(new Set(Double.valueOf(weightValue.getText().toString()),
                            Integer.valueOf(repsValue.getText().toString()),
                            exerciseSetArrayAdapter.getCount() + 1,
                            ""));
                    exerciseSetArrayAdapter.updateList(exercise.getSetList());
                    if (saveExerciseButton.getVisibility() != View.VISIBLE)
                        saveExerciseButton.show();
                }
                break;
            case R.id.save_exercise_button:
                progressBarDialog.show();
                listener.onSaveExercise(exercise);
                break;
            case R.id.set_delete:
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                int position = listView.getPositionForView(parentRow);

                exercise.getSetList().remove(position-1);
                for (int index=0; index < exercise.getSetList().size(); index++)
                    exercise.getSetList().get(index).setOrderNumber(index+1);

                exerciseSetArrayAdapter.updateList(exercise.getSetList());
                if (exercise.getSetList().size() == 0)
                    saveExerciseButton.hide();
                break;
            default:
                break;
        }


    }

    public void notifySetAddStatus(boolean status, FragmentManager fragmentManager) {
        progressBarDialog.dismiss();
        if (status){
            Toast.makeText(getContext(),
                R.string.exercise_added_success, Toast.LENGTH_LONG).show();
            fragmentManager.beginTransaction()
                    .remove(this)
                    .commit();
        } else {
            Toast.makeText(getContext(),
                    R.string.exercise_added_success, Toast.LENGTH_LONG).show();
        }
    }

    public void notifyLastExerciseFound(Exercise exercise) {
        progressBarDialog.dismiss();
        this.exercise = exercise;

        if (this.exercise != null) {
            title = getView().findViewById(R.id.title);
            typeValue = getView().findViewById(R.id.typeValue);
            targetValue = getView().findViewById(R.id.targetValue);

            title.setText(this.exercise.getName());
            typeValue.setText(this.exercise.getExerciseType());
            targetValue.setText(this.exercise.getExerciseTarget());

            exerciseSetArrayAdapter.updateList(this.exercise.getSetList());
            if (exercise.getSetList().size() > 0)
                saveExerciseButton.show();

        }
    }

    public Exercise getExercise() {
        return exercise;
    }


    class WeightDialogButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int value;
            RelativeLayout r = (RelativeLayout) v.getParent();

            TextView result = r.findViewById(R.id.weight_result);
            switch (v.getId()) {
                case R.id.inc_button:
                    if (result.getText().length() == 0) value = 0;
                    else value = Integer.valueOf(result.getText().toString());

                    if (++value > 10) value =10;
                    result.setText(String.valueOf(value));

                    break;
                case R.id.dec_button:
                    if (result.getText().length() == 0) value = 0;
                    else value = Integer.valueOf(result.getText().toString());

                    if (--value <  0) value = 0;
                    result.setText(String.valueOf(value));

                    break;
                default:
                    break;
            }

            calculateTotalWeight((LinearLayout) r.getParent());
        }
    }

    class ValueChangeWatcher implements TextWatcher {

        private Double max;
        private Double min;
        private Double multiple;
        private boolean backspaceFlag;

        public ValueChangeWatcher(double max, double min, double multiple) {
            this.max = Double.valueOf(max);
            this.min = Double.valueOf(min);
            this.multiple = Double.valueOf(multiple);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (before == 1 && count ==0)
                backspaceFlag = true;
            else
                backspaceFlag = false;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length()>1)
                s.setFilters(new InputFilter[]{new NumberFilter(max, min, multiple)});
            else
                s.setFilters(new InputFilter[]{});
            if (checkRepsField() && checkWeightField())
                setButtonDisabledColorFilter(saveSetButton,false);
            else
                setButtonDisabledColorFilter(saveSetButton,true);
            Log.d(TAG, "In afterTextChanged = " + s.toString());
        }

        class NumberFilter implements InputFilter {

            private Double maxValue;
            private Double minValue;
            private Double multipleValue;

            public NumberFilter(Double maxValue, Double minValue, Double multipleValue) {
                this.maxValue = maxValue;
                this.minValue = minValue;
                this.multipleValue = multipleValue;
            }



            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.toString().equalsIgnoreCase(".")||
                            source.toString().equalsIgnoreCase(","))// ||
                           // ((start == 0) && (end == 0) && (dend > dstart)))  //backspace
                    return null;

                try{
                    Double number = Double.parseDouble(dest.toString()+ source.toString() );
                    if (isInRange(number) && isMultipleOf(number))
                        return null;
                }catch(NumberFormatException nfe){
                    return "";
                }
                return "";
            }

            private boolean isInRange(Double number){
                if ((number.compareTo(maxValue) <= 0) && (number.compareTo((minValue)) >= 0))
                    return true;
                else {
                    Toast toast = Toast.makeText(getContext(), "Value is out of Range: "
                                    + minValue.intValue() + " - " + maxValue.intValue()
                            , Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundResource(R.color.colorPrimaryDark);
                    toast.show();
                    return false;
                }
            }

            private boolean isMultipleOf(Double number) {
                if ((number % multipleValue) == 0)
                    return true;
                else {
                    Toast toast = Toast.makeText(getContext(), "Value must be a multiple of : "
                            + multipleValue.doubleValue(), Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundResource(R.color.colorPrimaryDark);
                    toast.show();
                    return false;
                }

            }

        }
    }

    class ProgressBarDialog extends Dialog{

        public ProgressBarDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.progress_cyclic_dialog);
        }
    }

}
