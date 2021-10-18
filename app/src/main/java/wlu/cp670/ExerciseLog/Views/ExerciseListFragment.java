package wlu.cp670.ExerciseLog.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wlu.cp670.ExerciseLog.Models.ExerciseListItem;
import wlu.cp670.ExerciseLog.R;
import wlu.cp670.ExerciseLog.Utils.ExerciseFilter;
import wlu.cp670.ExerciseLog.Utils.ExerciseTarget;
import wlu.cp670.ExerciseLog.Utils.ExerciseType;

/*
    references:
        https://guides.codepath.com/android/Creating-and-Using-Fragments#fragment-listener
        https://www.tutorialspoint.com/how-can-i-add-items-to-a-spinner-in-android
 */
/**
 * Class ExerciseListFragment
 *
 * extends Fragment class
 *
 * layout: fragment_exercise_list.xml
 *
 * @author fort7350
 * @version 1.0
 */

public class ExerciseListFragment extends Fragment {

    private FragmentListener listener;
    private ExerciseItemArrayAdapter exerciseItemArrayAdapter;
    private ArrayList<ExerciseListItem> displayList;
    private RecyclerView recyclerView;
    private static final String TAG = "ExerciseListFragment";


    /**
     * Called by ExerciseActivity displayExerciseList(ExerciseListItem) method
     *
     * Creates the ExerciseItemArrayAdapter RecyclerView
     * and adds the passed ArrayList to the adapter
     *
     * @param exerciseList
     */
    public void displayList(ArrayList<ExerciseListItem> exerciseList){

        displayList = new ArrayList<>(exerciseList);
        exerciseItemArrayAdapter = new ExerciseItemArrayAdapter(getActivity(), displayList,
                new OnExerciseItemSelectedListener());

        recyclerView = getActivity().findViewById(R.id.recyclerViewExerciseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(exerciseItemArrayAdapter);
    }

    /**
     * Called by ExerciseActivity updateExerciseList(ArrayList<ExerciseListItem>) method
     *
     * clears the list and notifies the adapter of the new list
     *
     * @param exerciseList
     */
    public void updateList(ArrayList<ExerciseListItem> exerciseList){
        displayList.clear();
        displayList.addAll(exerciseList);
        exerciseItemArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Called by ExerciseActivity onOptionsItemSelected(MenuItem)
     *
     * displays the AlertDialog with layout provided by filter_dialog.xml
     */
    public void displayFilterDialog(){

        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        final View filterDialog = layoutInflater.inflate(R.layout.filter_dialog, null);

        Spinner typeSelect = filterDialog.findViewById(R.id.type_filter);
        setUpTypeFilter(typeSelect);

        Spinner targetSelect = filterDialog.findViewById(R.id.target_filter);
        setUpTargetFilter(targetSelect);

        AlertDialog.Builder builderCustom = new AlertDialog.Builder(this.getContext());
        AlertDialog alertDialog =  builderCustom.setView(filterDialog)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Map<ExerciseFilter, String> filter = new HashMap<>();

                        String typeValue = (String) typeSelect.getSelectedItem();
                        if(typeValue.length() > 0  && !(typeValue.equalsIgnoreCase("ALL"))){
                            filter.put(ExerciseFilter.TYPE, typeValue);
                        }

                        String targetValue =  (String) targetSelect.getSelectedItem();
                        if(targetValue.length() > 0  && !(targetValue.equalsIgnoreCase("ALL"))){
                            filter.put(ExerciseFilter.TARGET, targetValue);
                        }

                        if (filter.size()>0)
                            listener.onFilterSelected(filter);
                        else
                            listener.onClearFilter();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .create();

        int  displayMetrics = (int)getContext().getResources().getDisplayMetrics().density;
        TextView dialogTitle = new TextView(this.getContext());
        dialogTitle.setText(R.string.list_filter_dialog_title);
        dialogTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
        dialogTitle.setTextSize(20F);
        int pad  = 30 * displayMetrics;
        dialogTitle.setPadding(pad, pad, pad, pad);

        alertDialog.setCustomTitle(dialogTitle);
        alertDialog.show();
    }

    private void setUpTypeFilter(Spinner typeSelect){

        typeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.setSelection(position);
                ((TextView) parent.getSelectedView()).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("ALL");
        for (ExerciseType type: ExerciseType.values())
            typeList.add(type.toString());
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,typeList);
        typeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        typeSelect.setAdapter(typeAdapter);
    }


    private void setUpTargetFilter(Spinner targetSelect){

        targetSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                parent.setSelection(position);
                ((TextView) parent.getSelectedView()).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayList<String> targetList = new ArrayList<>();
        targetList.add("ALL");
        for (ExerciseTarget target: ExerciseTarget.values())
            targetList.add(target.toString());
        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,targetList);
        targetAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        targetSelect.setAdapter(targetAdapter);
    }


    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onCreateView()");
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onViewCreated()");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "In onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
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

    /**
     * Class OnExerciseItemSelectedListener
     *
     * Implements ExerciseItemArrayAdapter.OnItemClickListener
     *
     */
    class OnExerciseItemSelectedListener implements ExerciseItemArrayAdapter.OnItemClickListener {

        @Override
        public void onItemClick(ExerciseListItem exerciseListItem) {
            listener.onExerciseItemSelected(exerciseListItem);
        }
    }
}
