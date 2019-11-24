package wlu.cp670.fort7350_project.Views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.R;

/*
    references:
        https://guides.codepath.com/android/Creating-and-Using-Fragments#fragment-listener
 */

public class ExerciseListFragment extends Fragment{

    private OnExerciseItemSelectedListener listener;
    private ExerciseItemArrayAdapter exerciseItemArrayAdapter;
    RecyclerView recyclerView;
    private static final String TAG = "ExerciseListFragment";

    //listener to detect when item selected
    public interface OnExerciseItemSelectedListener {
        public void onExerciseItemSelected(int position, ArrayList<ExerciseListItem> exerciseListItems);
    }

    public void displayList(ArrayList<ExerciseListItem> exerciseList){

        exerciseItemArrayAdapter = new ExerciseItemArrayAdapter(getActivity(), exerciseList, listener);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerViewExerciseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(exerciseItemArrayAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "In onAttach()");
        super.onAttach(context);
        if (context instanceof OnExerciseItemSelectedListener) {
            listener = (OnExerciseItemSelectedListener) context;
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
}
