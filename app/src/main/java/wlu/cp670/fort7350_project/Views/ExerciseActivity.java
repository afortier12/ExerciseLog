package wlu.cp670.fort7350_project.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.Presenter.ExerciseData;
import wlu.cp670.fort7350_project.Presenter.ExerciseList;
import wlu.cp670.fort7350_project.Presenter.IExerciseData;
import wlu.cp670.fort7350_project.Presenter.IExerciseList;
import wlu.cp670.fort7350_project.R;

public class ExerciseActivity extends AppCompatActivity implements
        IExerciseList.View, IExerciseData.View,
        ExerciseListFragment.OnExerciseItemSelectedListener,
        ActivityListener {

    private static final String TAG = "Main Activity";
    private static final String LIST_FRAGMENT_TAG = "LIST_FRAGMENT";
    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT";
    private static final String DETAIL_ITEM_ARG = "DETAIL_ITEM";
    private static final String DETAIL_EXISTS_ARG = "DETAIL_EXITS";

    private ExerciseList exerciseListPresenter;
    private ExerciseData exerciseDataPresenter;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private ProgressBar progressBar;
    private TextView loadingText;
    private Toolbar appToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        if (savedInstanceState == null) {
            // Instance of first fragment
            ExerciseListFragment exerciseListFragment = new ExerciseListFragment();
            fragmentManager = getSupportFragmentManager();

            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    Log.d(TAG, "Fragment count in Back Stack: " + fragmentManager.getBackStackEntryCount());
                }
            });
            fragmentTransaction =
                    fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.flContainer, exerciseListFragment, LIST_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingText = (TextView) findViewById(R.id.loadingText);


        appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "In onDestroy()");
        exerciseListPresenter.stop();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "In onStart()");
        exerciseListPresenter = new ExerciseList(getFilesDir().getAbsolutePath(), this);
        exerciseListPresenter.loadDefaults(getApplicationContext());
        exerciseListPresenter.getExerciseList();

        exerciseDataPresenter = new ExerciseData(this, this.getApplicationContext());
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "In onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "In onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "In onPause()");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "In onRestart()");
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_filter) {
            Log.d(TAG, "filter selected");
        } else if (id == R.id.action_edit) {

        } else if (id == R.id.action_settings) {

        } else {

        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void updateProgressBar(int value) {
        progressBar.setProgress(value);
    }

    @Override
    public void showProgressBar(int value) {
        progressBar.setVisibility(value);
        loadingText.setVisibility(value);
    }

    @Override
    public void displayExerciseList(ArrayList<ExerciseListItem> exerciseList) {

        ExerciseListFragment exerciseListFragment =
                (ExerciseListFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);

        if (exerciseListFragment != null) {
            exerciseListFragment.displayList(exerciseList);
        }

    }

    @Override
    public void onExerciseItemSelected(int position, ArrayList<ExerciseListItem> exerciseList) {
        Log.d(TAG, "In onExerciseItemSelected");

        ExerciseListItem exerciseListItem = exerciseList.get(position);


        //check if exercise data exists in database
        boolean exists = exerciseDataPresenter.checkLastExercise(exerciseListItem.getName());
        Exercise exercise = new Exercise(exerciseListItem.getName(),
                exerciseListItem.getExerciseType(), exerciseListItem.getExerciseTarget());

        displayExerciseData(exercise, exists);

    }

    @Override
    public void displayFilteredExerciseList(ArrayList<ExerciseListItem> exerciseList) {

    }

    @Override
    public void displayExerciseData(Exercise exercise, boolean exists) {

        //create ExerciseDataFragment object
        ExerciseDataFragment exerciseDataFragment = new ExerciseDataFragment();

        //create Bundle with selected item
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_ITEM_ARG, exercise);
        args.putBoolean(DETAIL_EXISTS_ARG, exists);
        //pass Bundle to fragment
        exerciseDataFragment.setArguments(args);

        //TODO
        // add condition for dual fragment layout

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, exerciseDataFragment, DETAIL_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public int addSet(Set set) {
        return 0;
    }

    @Override
    public int updateSet(Set set) {
        return 0;
    }

    @Override
    public int deleteSet(Set set) {
        return 0;
    }

    @Override
    public void onExerciseSetSelected(int position, ArrayList<Set> exerciseSetItems) {

    }

    /**
     * Implementation of onGetLastData of ActivityListener
     * fragment request of exercise data from last workout
     * @param exercise
     */
    @Override
    public void onGetLastData(Exercise exercise) {
        exerciseDataPresenter.getLastExerciseData(exercise);
    }

}

