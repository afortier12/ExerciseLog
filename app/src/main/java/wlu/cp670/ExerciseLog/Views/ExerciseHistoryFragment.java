package wlu.cp670.ExerciseLog.Views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wlu.cp670.ExerciseLog.Models.Set;
import wlu.cp670.ExerciseLog.R;


/**
 * Class ExerciseHistoryFragment
 *
 * extends Fragment class
 *
 * layout: fragment_exercise_history.xml
 *
 * @author fort7350
 * @version 1.0
 */
public class ExerciseHistoryFragment extends Fragment implements View.OnClickListener {

    private FragmentListener listener;
    private ExerciseHistoryArrayAdapter exerciseHistoryArrayAdapter;
    private ArrayList<Set> displayList;
    private RecyclerView recyclerView;
    private static final String TAG = "ExerciseHistoryFragment";
    private static final String DETAIL_ITEM_ARG = "DETAIL_ITEM";
    private static final String DATE_FORMAT_DISPLAY = "MM/dd/yyyy";
    private static final String DATE_FORMAT_DB = "yyyy-MM-dd";

    TextView title;
    EditText startDate;
    EditText endDate;
    ImageButton startDateButton;
    ImageButton endDateButton;
    Button searchButton;
    private String exerciseSelected = "";
    Calendar calendar = Calendar.getInstance();


    /**
     *
     * Called by ExerciseActivity onHistoryFound method
     *
     * @param displayList
     */
    public void updateHistoryList(ArrayList<Set> displayList){
        this.displayList.clear();
        //empty set for header row
        this.displayList.add(new Set(0.0,0,0,""));
        this.displayList.addAll(displayList);
        exerciseHistoryArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "In onAttach()");
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ExerciseList.OnItemSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onCreate)");
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // Get back arguments
            if (getArguments() != null) {
                exerciseSelected = getArguments().getString(DETAIL_ITEM_ARG);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onCreateView()");

        displayList = new ArrayList<>();

        if (savedInstanceState != null) {
            exerciseSelected = savedInstanceState.getString(DETAIL_ITEM_ARG, "");
            displayList = savedInstanceState.getParcelableArrayList("DISPLAY_LIST");
        }

        View view = inflater.inflate(R.layout.fragment_exercise_history, container, false);

        title = view.findViewById(R.id.title);
        startDate = view.findViewById(R.id.startEditText);
        startDate.addTextChangedListener(new DateTextWatcher());
        endDate = view.findViewById(R.id.endEditText);
        endDate.addTextChangedListener(new DateTextWatcher());

        startDateButton = view.findViewById(R.id.startDateButton);
        startDateButton.setOnClickListener(this);
        endDateButton = view.findViewById(R.id.endDateButton);
        endDateButton.setOnClickListener(this);

        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        setButtonDisabledColorFilter(searchButton, true);

        //empty set for header row
        displayList.add(new Set(0.0, 0,0,""));
        exerciseHistoryArrayAdapter = new ExerciseHistoryArrayAdapter(displayList);
        recyclerView = view.findViewById(R.id.recyclerViewHistoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(exerciseHistoryArrayAdapter);

        if (savedInstanceState != null)
            exerciseHistoryArrayAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "In onSaveInstanceState()");
        outState.putString(DETAIL_ITEM_ARG, exerciseSelected);
        outState.putParcelableArrayList("DISPLAY_LIST", displayList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onViewCreated()");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "In onResume()");
        super.onResume();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.startDateButton:
                startDate.setError(null);
                DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay(startDate);
                    }
                };

                new DatePickerDialog(getContext(), startDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
            case R.id.endDateButton:
                endDate.setError(null);
                DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay(endDate);
                    }
                };

                new DatePickerDialog(getContext(), endDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
            case R.id.search_button:
                boolean searchOkFlag = true;
                String startEntry = startDate.getText().toString();
                if (isDateNotValid(startEntry)){
                    startDate.setError("Error");
                    searchOkFlag = false;
                }
                String endEntry = endDate.getText().toString();
                if (isDateNotValid(endEntry)){
                    endDate.setError("Error");
                    searchOkFlag = false;
                }

                if (searchOkFlag) {
                    if (compareDate(startEntry, endEntry)) {
                        endDate.setError("Must be greater than start date");
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DISPLAY,
                                Locale.getDefault());
                        try {
                            Date date = dateFormat.parse(startEntry);
                            dateFormat.applyLocalizedPattern(DATE_FORMAT_DB);
                            String startEntryDBFormat = dateFormat.format(date);

                            dateFormat.applyLocalizedPattern(DATE_FORMAT_DISPLAY);
                            date = dateFormat.parse(endEntry);
                            dateFormat.applyLocalizedPattern(DATE_FORMAT_DB);
                            String endEntryDBFormat = dateFormat.format(date);

                            listener.onSearchHistory(exerciseSelected, startEntryDBFormat,
                                    endEntryDBFormat);
                        } catch(ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }

    }

    /**
     * Check if date entered is not valid
     * @param date
     * @return true if not valid
     */
    private boolean isDateNotValid(String date){

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DISPLAY,
                Locale.getDefault());
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
        }catch (ParseException e){
            return true;
        }
        return false;
    }

    /**
     * Compares two dates passed to the function as String objects
     *
     * @param start
     * @param end
     * @return true if start is after end
     */
    private boolean compareDate(String start, String end){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DISPLAY,
                Locale.getDefault());
        dateFormat.setLenient(false);
        try {
            Date startDate = dateFormat.parse(start);
            Date endDate = dateFormat.parse(end);

            if (startDate != null && endDate != null)
                return startDate.after(endDate);
            else
                return false;
        }catch (ParseException e){
            return false;
        }
    }

    /**
     * Called after DatePicker Dialog is updated
     *
     *
     * @param dateEditText
     */
    private void updateDateDisplay(EditText dateEditText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DISPLAY,
                Locale.getDefault());
        dateEditText.setText(dateFormat.format(calendar.getTime()));
    }

    /**
     * Greys out the button if setFlag is true
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

    /**
     * Class DateTextWatcher
     *
     * Implements TextWatcher for EditText widgets
     *
     *
     * @author fort7350
     * @version 1.0
     */
    class DateTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String startEntry = startDate.getText().toString();
            String endEntry = endDate.getText().toString();
            if (isDateNotValid(startEntry) || isDateNotValid(endEntry))
                setButtonDisabledColorFilter(searchButton,true);
            else
                setButtonDisabledColorFilter(searchButton,false);
            Log.d(TAG, "In afterTextChanged = " + s.toString());
        }
    }
}
