package wlu.cp670.fort7350_project.Views;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.os.Bundle;
        import android.text.Editable;
        import android.text.InputFilter;
        import android.text.Spanned;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
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
        import androidx.fragment.app.Fragment;

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

public class ExerciseDataFragment extends Fragment implements View.OnClickListener, FragmentListener {

    private static final String TAG = "ExerciseDataFragment";
    private static final String DETAIL_ITEM_ARG = "DETAIL_ITEM";
    private static final String DETAIL_EXISTS_ARG = "DETAIL_EXITS";

    private Exercise exercise;
    private boolean exercise_exists;

    private ExerciseSetArrayAdapter exerciseSetArrayAdapter;
    private ActivityListener listener;

    private TextView title;
    private TextView targetValue;
    private TextView typeValue;
    private EditText weightValue;
    private EditText repsValue;
    private ImageButton weightButton;
    private ImageButton repsIncreaseButton;
    private ImageButton repsDecreaseButton;
    private Button saveSetButton;
    private ProgressBar progressBarCyclic;
    private ListView listView;

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
        //listView.setBackgroundColor(getResources().getColor(android.R.color.white));

        exerciseSetArrayAdapter = new ExerciseSetArrayAdapter(getActivity(), exercise.getSetList());
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
        repsValue.addTextChangedListener(new ValueChangeWatcher(100,1,1));

        weightValue = view.findViewById(R.id.weightEditText);
        weightValue.addTextChangedListener(new ValueChangeWatcher(1000, 0, 2.5));

        saveSetButton = view.findViewById(R.id.save_set_button);
        saveSetButton.setOnClickListener(this);

        progressBarCyclic = (ProgressBar) view.findViewById(R.id.progressBar_cyclic);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onViewCreated()");
        if (exercise_exists){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.exercise_exists)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressBarCyclic.setVisibility(View.VISIBLE);
                        //callback to ExerciseActivity to get last data
                        listener.onGetLastData(exercise);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
        textView.setText(String.valueOf(total)+"LBS");
    }

    private boolean checkDataFields(){

        boolean flag = false;
        String weightEntry = weightValue.getText().toString();
        if (weightEntry.isEmpty()) weightEntry="0";
        if (Integer.valueOf(weightEntry) <= 0)
            weightValue.setError("Error");
        else flag = true;

        String repsEntry = repsValue.getText().toString();
        if (repsEntry.isEmpty()) repsEntry="0";
        if (Integer.valueOf(repsEntry) <= 0) {
            repsValue.setError("Error");
        } else flag = flag && true;


        return flag;

    }

    @Override
    public void onClick(View v) {
        int value;
        switch (v.getId()) {
            case R.id.weightAddButton:
                displayWeightDialog(v);
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
                if (checkDataFields()) {
                    exercise.addSet(new Set(Double.valueOf(weightValue.getText().toString()),
                            Integer.valueOf(repsValue.getText().toString()),
                            exerciseSetArrayAdapter.getCount() + 1));
                    exerciseSetArrayAdapter.updateList(exercise.getSetList());
                    int checkcount = exerciseSetArrayAdapter.getCount();
                } else {

                    //TODO snack bar message
                }
                break;

            case R.id.save_exercise_button:

                break;

            default:
                break;
        }
    }


    @Override
    public void updateSetList(Exercise exercise) {
        progressBarCyclic.setVisibility(View.INVISIBLE);
        exerciseSetArrayAdapter.updateList(exercise.getSetList());
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

        }

        @Override
        public void afterTextChanged(Editable s) {
            s.setFilters(new InputFilter[]{new NumberFilter(max, min, multiple)});
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

}
