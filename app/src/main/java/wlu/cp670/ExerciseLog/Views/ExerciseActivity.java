package wlu.cp670.ExerciseLog.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import wlu.cp670.ExerciseLog.Models.Exercise;
import wlu.cp670.ExerciseLog.Models.ExerciseListItem;
import wlu.cp670.ExerciseLog.Models.Set;
import wlu.cp670.ExerciseLog.Presenter.ExerciseData;
import wlu.cp670.ExerciseLog.Presenter.ExerciseList;
import wlu.cp670.ExerciseLog.Presenter.IExerciseData;
import wlu.cp670.ExerciseLog.Presenter.IExerciseList;
import wlu.cp670.ExerciseLog.R;
import wlu.cp670.ExerciseLog.Utils.ExerciseFilter;

/**
 * Class ExerciseActivity
 *
 * layout: activity_exercise.xml
 * @see wlu.cp670.ExerciseLog.Presenter.IExerciseList.View
 * @see wlu.cp670.ExerciseLog.Presenter.IExerciseData.View
 * @see wlu.cp670.ExerciseLog.Presenter.IExerciseData.View.History
 * @see wlu.cp670.ExerciseLog.Views.FragmentListener
 * @author fort7350
 * @version 1.0
 */

public class ExerciseActivity extends AppCompatActivity implements
        IExerciseList.View, IExerciseData.View, IExerciseData.View.History,
        FragmentListener {

    private static final String TAG = "Main Activity";
    private static final String LIST_FRAGMENT_TAG = "LIST_FRAGMENT";
    private static final String DATA_FRAGMENT_TAG = "DETAIL_FRAGMENT";
    private static final String HISTORY_FRAGMENT_TAG = "HISTORY_FRAGMENT";
    private static final String DETAIL_ITEM_ARG = "DETAIL_ITEM";
    private static final String DETAIL_EXISTS_ARG = "DETAIL_EXITS";
    private static final String EXERCISE_LIST_ARG = "EXERCISE_LIST";
    private static final int REQUEST_CODE_IMPORT_EXPORT = 8;

    private ExerciseList exerciseListPresenter;
    private ExerciseData exerciseDataPresenter;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ExerciseListFragment exerciseListFragment;
    private ExerciseDataFragment exerciseDataFragment;
    private ExerciseHistoryFragment exerciseHistoryFragment;

    private ProgressDialog progressDialog;
    private ProgressDialogNoCount progressDialogNoCount;


    private boolean showFilter = true;
    private boolean showHistory = false;
    private boolean showList = false;
    private boolean reloadList = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        if (savedInstanceState == null) {

            // Instance of first fragment
            exerciseListFragment = new ExerciseListFragment();
            fragmentManager = getSupportFragmentManager();

            fragmentManager.addOnBackStackChangedListener(new FragmentBackStackListener()) ;
            fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.flContainer, exerciseListFragment, LIST_FRAGMENT_TAG)
                .commit();
        } else {
            fragmentManager = getSupportFragmentManager();
           // exerciseListFragment = (ExerciseListFragment) fragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG);
           // exerciseDataFragment = (ExerciseDataFragment) fragmentManager.findFragmentByTag(DATA_FRAGMENT_TAG);
           // exerciseHistoryFragment = (ExerciseHistoryFragment) fragmentManager.findFragmentByTag(HISTORY_FRAGMENT_TAG);
            showFilter = savedInstanceState.getBoolean("FILTER", false);
            showHistory = savedInstanceState.getBoolean("HISTORY", false);
            showList = savedInstanceState.getBoolean("LIST", false);
        }

        exerciseListPresenter = new ExerciseList(getFilesDir().getAbsolutePath(), this);
        exerciseDataPresenter = new ExerciseData(this, this,
                this.getApplicationContext());

        progressDialog = new ProgressDialog(this);
        progressDialogNoCount = new ProgressDialogNoCount(this);

        Toolbar appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        invalidateOptionsMenu();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(EXERCISE_LIST_ARG, exerciseListPresenter.getExerciseList());
        savedInstanceState.putBoolean("FILTER", showFilter);
        savedInstanceState.putBoolean("HISTORY", showHistory);
        savedInstanceState.putBoolean("LIST", showList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 2){
            showFilter = false;
            showHistory = true;
            showList = true;
            invalidateOptionsMenu();

        } else if (fragmentManager.getBackStackEntryCount() == 1) {
            Fragment fragment = fragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG);
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .hide(fragment)
                        .commit();
                showFilter = false;
            }
            showFilter = true;
            showHistory = false;
            showList = false;
            invalidateOptionsMenu();
            fragmentManager.popBackStack(LIST_FRAGMENT_TAG,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            exerciseListPresenter.updateExerciseList(exerciseListPresenter.getExerciseList());
        }

        super.onBackPressed();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (!reloadList && exerciseListPresenter != null)
            exerciseListPresenter.updateExerciseList(savedInstanceState.
                    getParcelableArrayList(EXERCISE_LIST_ARG));
        showFilter = savedInstanceState.getBoolean("FILTER", false);
        showHistory = savedInstanceState.getBoolean("HISTORY", false);
        showList = savedInstanceState.getBoolean("LIST", false);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMPORT_EXPORT && resultCode == RESULT_OK){
            if (data.getBooleanExtra(ImportExportActivity.RESULT_CODE_IMPORT, false))
                reloadList = true;
        }

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
        exerciseListPresenter.loadDefaults(getApplicationContext());
        if (reloadList || (exerciseListPresenter.getExerciseList() == null)) {
            exerciseListPresenter.readExerciseListFromFile();
            reloadList = false;
        } else if (exerciseListPresenter.getExerciseList() != null) {
            displayExerciseList(exerciseListPresenter.getExerciseList());
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "In onPause()");
        super.onPause();
    }

    /**
     * Update Intent when activity started
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    /**
     * Check if list needs to be reloaded when ExerciseListFragment is displayed
     */
    @Override
    protected void onRestart() {
        Log.d(TAG, "In onRestart()");
        Intent intent = getIntent();
        if (intent != null ){
            if (intent.hasExtra(ImportExportActivity.RESULT_CODE_IMPORT))
                reloadList = intent.getBooleanExtra(ImportExportActivity.RESULT_CODE_IMPORT,
                        false);
        }
        Objects.requireNonNull(intent).removeExtra(ImportExportActivity.RESULT_CODE_IMPORT);
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
            if (fragmentManager.getBackStackEntryCount() > 0){
                showList = false;
                showFilter = true;
                showHistory = false;
                invalidateOptionsMenu();
                fragmentManager.popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }

        } else if (id == R.id.action_tools) {
            Intent intent = new Intent(this, ImportExportActivity.class);
            startActivityForResult(intent, REQUEST_CODE_IMPORT_EXPORT);

        } else if (id == R.id.action_history){
            Log.d(TAG, "history selected");
            displayHistoryData(exerciseDataFragment.getExercise());

        }else if (id == R.id.action_about){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.about_message_title));
            builder.setMessage(Html.fromHtml(getResources().getString(R.string.about_message)));
            builder.setPositiveButton("Yes",null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.findItem(R.id.action_history).setVisible(showHistory);
        menu.findItem(R.id.action_filter).setVisible(showFilter);
        menu.findItem(R.id.action_list).setVisible(showList);
        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * Implements IExerciseList.View method
     *
     * Called by XMLParse class
     *
     * Updates the count and total on the ProgressDialog
     *
     * @param count
     * @param total
     */
    @Override
    public void updateProgressBar(int count, int total) {
        progressDialog.updateCounts(count, total);
    }

    /**
     * Implements IExerciseList.View method
     *
     * Called by XMLParse class
     *
     * Shows the layout from the ProgressDialog class
     *
     */
    @Override
    public void showProgressBar() {
        progressDialog.show();
    }


    /**
     * Implements IExerciseList.View method
     *
     * Called by XMLParse class onPostExecute()
     *
     * @param exerciseList
     */
    @Override
    public void displayExerciseList(ArrayList<ExerciseListItem> exerciseList) {
        progressDialog.dismiss();
        if (exerciseListFragment != null)
            exerciseListFragment.displayList(exerciseList);

    }

    /**
     * Implements IExerciseList.View method
     *
     * Called by ExerciseList clearExerciseListFilter()
     * and filterExerciseList(Map<ExerciseList, String>) methods
     *
     * @param exerciseList
     */
    @Override
    public void updateExerciseList(ArrayList<ExerciseListItem> exerciseList) {
        if (exerciseListFragment != null)
            exerciseListFragment.updateList(exerciseList);
    }

    /**
     * Implements IExerciseData.View method
     *
     * Called by ExerciseActivity displayExerciseData() method
     *
     * @param exercise
     * @param exists
     */
    @Override
    public void displayExerciseData(Exercise exercise, boolean exists) {

        //create ExerciseDataFragment object
        exerciseDataFragment = new ExerciseDataFragment();

        showList = true;
        showFilter =false;
        showHistory = true;
        invalidateOptionsMenu();

        //create Bundle with selected item
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_ITEM_ARG, exercise);
        args.putBoolean(DETAIL_EXISTS_ARG, exists);
        //pass Bundle to fragment
        exerciseDataFragment.setArguments(args);

        fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment: fragmentManager.getFragments())
            fragmentTransaction.hide(fragment);
        fragmentTransaction.add(R.id.flContainer, exerciseDataFragment, DATA_FRAGMENT_TAG)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        invalidateOptionsMenu();

    }


    /**
     * Implements IExerciseData.View method
     *
     * Called by ExerciseData addExerciseSetData(Exercise) method
     *
     * @param status
     */
    @Override
    public void onExerciseSetDataAdded(boolean status) {
        exerciseDataFragment.notifySetAddStatus(status, fragmentManager);

    }

    /**
     * Implements IExerciseData.View method
     *
     * Called by ExerciseData getLastExerciseData(Exercise) method
     *
     * @param exercise
     */
    @Override
    public void onGetLastExerciseComplete(Exercise exercise) {

        exerciseDataFragment.notifyLastExerciseFound(exercise);

    }

    /**
     * Implements IExerciseData.View.History method
     *
     * Called byExerciseActivity onOptionsItemSelectedMethod(MenuItem)
     *
     * @param exercise
     */
    @Override
    public void displayHistoryData(Exercise exercise) {
        //create ExerciseDataFragment object
        exerciseHistoryFragment = new ExerciseHistoryFragment();

        showHistory = false;
        showList = true;
        showFilter = false;
        invalidateOptionsMenu();

        //create Bundle with selected item
        Bundle args = new Bundle();
        args.putString(DETAIL_ITEM_ARG, exercise.getName());
        //pass Bundle to fragment
        exerciseHistoryFragment.setArguments(args);


        fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment: fragmentManager.getFragments())
            fragmentTransaction.hide(fragment);
        fragmentTransaction.add(R.id.flContainer, exerciseHistoryFragment, HISTORY_FRAGMENT_TAG)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        invalidateOptionsMenu();
    }

    /**
     * Implements IExerciseData.View.History method
     *
     * Called by ReadFromDB AsyncTask onPostExecute() method
     *
     * @param displayList
     */
    @Override
    public void onHistoryFound(ArrayList<Set> displayList) {
        progressDialogNoCount.dismiss();
        exerciseHistoryFragment.updateHistoryList(displayList);
    }

    /**
     * Implements IExerciseData.View.History method
     *
     * Called by ReadFromDB AsyncTask
     *
     */
    @Override
    public void showProgress() {
        progressDialogNoCount.show();
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

    /**
     * Implementation of onExerciseItemSelected of FragmentListener
     *
     * Called by ExerciseListFragment onItemClick
     *
     * @param exerciseListItem
     */
    @Override
    public void onExerciseItemSelected(ExerciseListItem exerciseListItem) {
        //check if exercise data exists in database
        boolean exists = exerciseDataPresenter.checkLastExercise(exerciseListItem.getName());
        Exercise exercise = new Exercise(exerciseListItem.getName(),
                exerciseListItem.getExerciseType(), exerciseListItem.getExerciseTarget());

        displayExerciseData(exercise, exists);
    }

    /**
     * Implementation of onFilterSelected of FragmentListener
     *
     * Called by ExerciseListFragment displayFilterDialog method
     *
     * @param filter
     */
    @Override
    public void onFilterSelected(Map<ExerciseFilter, String> filter) {
        exerciseListPresenter.filterExerciseList(filter);
    }

    /**
     * Implementation of onFilterSelected of FragmentListener
     *
     * Called by ExerciseListFragment displayFilterDialog method
     */
    @Override
    public void onClearFilter() {
        exerciseListPresenter.clearExerciseListFilter();
    }

    /**
     * Implementation of onSearchHistory of FragmentListener
     *
     * Called by ExerciseHistoryFragment onClick() method
     *
     * @param exerciseName
     * @param startDate
     * @param endDate
     */
    @Override
    public void onSearchHistory(String exerciseName, String startDate, String endDate) {

        Map<ExerciseFilter, String> filter = new HashMap<>();

        filter.put(ExerciseFilter.DATE_START, startDate);
        endDate = endDate + " 23:59:59";
        filter.put(ExerciseFilter.DATE_END, endDate);

        ArrayList<Set> list = new ArrayList<>();
        exerciseDataPresenter.getExerciseByFilter(filter, exerciseName, list, null);

    }


    /**
     * Class FragmentBackStackListener
     *
     * Implements FragmentManager.OnBackStackChangedListener
     *
     * @author fort7350
     * @version 1.0
     */

    class FragmentBackStackListener implements FragmentManager.OnBackStackChangedListener {

        @Override
        public void onBackStackChanged() {
            Log.d(TAG, "Fragment count in Back Stack: " + fragmentManager.getBackStackEntryCount());
            if (fragmentManager.getBackStackEntryCount() == 1){
                Fragment fragment = fragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG);
                if (fragment != null) {
                    fragmentManager.beginTransaction()
                            .hide(fragment)
                            .commit();
                    showFilter = false;
                }
                showHistory = true;
                showList = true;
                invalidateOptionsMenu();
            } else if (fragmentManager.getBackStackEntryCount() == 2){
                showFilter = false;
                showHistory = false;
                showList = true;
                invalidateOptionsMenu();
            } else if (fragmentManager.getBackStackEntryCount() == 0){
                showFilter = true;
                showHistory = false;
                showList = false;
                invalidateOptionsMenu();
            }

        }
    }

    /**
     * Class ProgressDialogNoCount
     *
     * extends Dialog class
     *
     * layout:progress_cyclic_dialog.xml
     *
     * @author fort7350
     * @version 1.0
     */

    class ProgressDialogNoCount extends Dialog {

        public ProgressDialogNoCount(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.progress_cyclic_dialog);
            setCancelable(false);

        }

    }


    /**
     * Class ProgressDialogNoCount
     *
     * extends Dialog class
     *
     * layout:progress_cyclic_dialog_import.xml
     *
     * @author fort7350
     * @version 1.0
     */
    class ProgressDialog extends Dialog {

        TextView count;
        TextView total;

        public ProgressDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.progress_cyclic_dialog_import);
            setCancelable(false);

            count = findViewById(R.id.progress_count);
            count.setText("0");
            total = findViewById(R.id.progress_total);
            total.setText("0");

        }

        public void updateCounts(int countValue, int totalValue){
            count.setText(String.valueOf(countValue));
            total.setText(String.valueOf(totalValue));
        }
    }


}

