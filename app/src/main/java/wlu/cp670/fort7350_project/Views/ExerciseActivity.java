package wlu.cp670.fort7350_project.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.Presenter.ExerciseData;
import wlu.cp670.fort7350_project.Presenter.ExerciseList;
import wlu.cp670.fort7350_project.Presenter.IExerciseData;
import wlu.cp670.fort7350_project.Presenter.IExerciseList;
import wlu.cp670.fort7350_project.R;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;

public class ExerciseActivity extends AppCompatActivity implements
        IExerciseList.View, IExerciseData.View, IExerciseData.View.History,
        FragmentListener {

    private static final String TAG = "Main Activity";
    private static final String LIST_FRAGMENT_TAG = "LIST_FRAGMENT";
    private static final String DATA_FRAGMENT_TAG = "DETAIL_FRAGMENT";
    private static final String HISTORY_FRAGMENT_TAG = "HISTORY_FRAGMENT";
    private static final String DETAIL_ITEM_ARG = "DETAIL_ITEM";
    private static final String DETAIL_EXISTS_ARG = "DETAIL_EXITS";

    private ExerciseList exerciseListPresenter;
    private ExerciseData exerciseDataPresenter;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ExerciseListFragment exerciseListFragment;
    private ExerciseDataFragment exerciseDataFragment;
    private ExerciseHistoryFragment exerciseHistoryFragment;

    private ProgressBar progressBar;
    private TextView loadingText;
    private Toolbar appToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        if (savedInstanceState == null) {
            // Instance of first fragment
            exerciseListFragment = new ExerciseListFragment();
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

        /*

        /To add an item
toolbar.getMenu().add(Menu.NONE,FIRST_ID,Menu.NONE,R.string.placeholder1);
toolbar.getMenu().add(Menu.NONE,SECOND_ID,Menu.NONE,R.string.placeholder2);

//and to remove a element just remove it with id
toolbar.getMenu().removeItem(FIRST_ID);/To add an item
toolbar.getMenu().add(Menu.NONE,FIRST_ID,Menu.NONE,R.string.placeholder1);
toolbar.getMenu().add(Menu.NONE,SECOND_ID,Menu.NONE,R.string.placeholder2);

//and to remove a element just remove it with id
toolbar.getMenu().removeItem(FIRST_ID);
         */

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingText = (TextView) findViewById(R.id.loadingText);


        appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);

    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getFragments().size() == 1){
            finish();
        }
        super.onBackPressed();
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
        exerciseListPresenter.readExerciseListFromFile();

        exerciseDataPresenter = new ExerciseData(this, this, this.getApplicationContext());
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
        inflater.inflate(R.menu.toolbar_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_filter) {
            Log.d(TAG, "filter selected");
            exerciseListFragment.displayFilterDialog();

        } else if (id == R.id.action_list) {

        } else if (id == R.id.action_tools) {

        } else if (id == R.id.action_history){
            Log.d(TAG, "history selected");
            Fragment fragment = fragmentManager.findFragmentByTag(DATA_FRAGMENT_TAG);
            if (fragment != null) {
                if (fragment.isVisible()){
                    displayHistoryData(exerciseDataFragment.getExercise());
                } else{
                    //launch history activity
                }
            }
        }else {

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

        if (exerciseListFragment != null)
            exerciseListFragment.displayList(exerciseList);

    }

    @Override
    public void updateExerciseList(ArrayList<ExerciseListItem> exerciseList) {
        if (exerciseListFragment != null)
            exerciseListFragment.updateList(exerciseList);
    }

    @Override
    public void displayExerciseData(Exercise exercise, boolean exists) {

        //create ExerciseDataFragment object
        exerciseDataFragment = new ExerciseDataFragment();

        //create Bundle with selected item
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_ITEM_ARG, exercise);
        args.putBoolean(DETAIL_EXISTS_ARG, exists);
        //pass Bundle to fragment
        exerciseDataFragment.setArguments(args);

        //TODO
        // add condition for dual fragment layout

        fragmentManager
                .beginTransaction()
                .add(R.id.flContainer, exerciseDataFragment, DATA_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();

    }



    @Override
    public void onExerciseSetDataAdded(boolean status) {

        exerciseDataFragment.notifySetAddStatus(status, fragmentManager);

    }

    @Override
    public void onGetLastExerciseComplete(Exercise exercise) {

        exerciseDataFragment.notifyLastExerciseFound(exercise);

    }

    @Override
    public void displayHistoryData(Exercise exercise) {
        //create ExerciseDataFragment object
        exerciseHistoryFragment = new ExerciseHistoryFragment();

        //create Bundle with selected item
        Bundle args = new Bundle();
        args.putString(DETAIL_ITEM_ARG, exercise.getName());
        //pass Bundle to fragment
        exerciseHistoryFragment.setArguments(args);

        //TODO
        // add condition for dual fragment layout

        fragmentManager
                .beginTransaction()
                .add(R.id.flContainer, exerciseHistoryFragment, HISTORY_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onHistoryFound(ArrayList<Set> displayList) {
        exerciseHistoryFragment.updateHistoryList(displayList);
    }

    @Override
    public void addSet(Set set) {

    }

    @Override
    public void updateSet(Set set) {

    }

    @Override
    public void deleteSet(Set set) {

    }


    /**
     * Implementation of onSaveExercise of FragmentListener
     * Called by ExerciseDataFragment
     * @param exercise
     */
    @Override
    public void onSaveExercise(Exercise exercise) {
        exerciseDataPresenter.addExerciseSetData(exercise);
    }

    /**
     * Implementation of onGetLastData of FragmentListener
     * fragment request of exercise data from last workout
     * @param exercise
     */
    @Override
    public void onGetLastData(Exercise exercise) {
        exerciseDataPresenter.getLastExerciseData(exercise);
    }

    @Override
    public void onExerciseItemSelected(ExerciseListItem exerciseListItem) {
        //check if exercise data exists in database
        boolean exists = exerciseDataPresenter.checkLastExercise(exerciseListItem.getName());
        Exercise exercise = new Exercise(exerciseListItem.getName(),
                exerciseListItem.getExerciseType(), exerciseListItem.getExerciseTarget());

        displayExerciseData(exercise, exists);
    }

    @Override
    public void onFilterSelected(Map<ExerciseFilter, String> filter) {
        exerciseListPresenter.filterExerciseList(filter);
    }

    @Override
    public void onClearFilter() {
        exerciseListPresenter.clearExerciseListFilter();
    }

    @Override
    public void onSearchHistory(String exerciseName, String startDate, String endDate) {

        Map<ExerciseFilter, String> filter = new HashMap<>();

        filter.put(ExerciseFilter.DATE_START, startDate);
        filter.put(ExerciseFilter.DATE_END, endDate);

        ArrayList<Set> list = new ArrayList<>();
        exerciseDataPresenter.getExerciseByFilter(filter, exerciseName, list);

    }


}

