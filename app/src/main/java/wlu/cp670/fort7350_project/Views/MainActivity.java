package wlu.cp670.fort7350_project.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Presenter.ExerciseList;
import wlu.cp670.fort7350_project.Presenter.IExerciseList;
import wlu.cp670.fort7350_project.R;

public class MainActivity extends AppCompatActivity implements IExerciseList.View {

    private ExerciseList exerciseListPresenter;
    private static final String TAG = "Main Activity";

    ProgressBar progressBar;
    RecyclerView recyclerView;
    Toolbar appToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDefaults();

        /*
        TODO
        add snackbar if exercise.xml created or missing
        trigger copy if missing??
        */

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);

        exerciseListPresenter = new ExerciseList(getFilesDir().getAbsolutePath(), this);
        exerciseListPresenter.getExerciseList();

    }

    @Override
    protected void onDestroy() {
        exerciseListPresenter.stop();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        if (id == R.id.action_filter){
            Log.d(TAG, "filter selected");
        } else if (id == R.id.action_edit){

        } else if (id == R.id.action_settings){

        } else {

        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void loadDefaults(){
        //get the shared preference file
        String preferenceFile = getString(R.string.preference_name);
        SharedPreferences sharedPreferences = getSharedPreferences(preferenceFile, Context.MODE_PRIVATE);

        //get the key and value
        String loadedKey = getString(R.string.preference_key_default_loaded);
        boolean keyValue = sharedPreferences.getBoolean(loadedKey, false );

        //get reference to exercises.xml file
        String fileName = "exercises.xml";
        File file = new File(getFilesDir(), fileName);

        //default has not been loaded or file does not exist
        if (!keyValue || !file.exists() ){
            //get editor for shared preference file
            SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
            preferenceEditor.clear();

            if (!file.exists()){
                //copy the default xml file in assets folder to internal storage
                try{
                    AssetManager assetManager = getAssets();
                    InputStream defaultXML = assetManager.open("exercises.xml");
                    copyDefaultToInternalStorage(defaultXML, fileName);
                }catch(Exception e) {
                    Log.e(TAG, "Error: default exercise list is missing. "
                            + e.getMessage());
                }
            }

            preferenceEditor.putBoolean(loadedKey, true);
            preferenceEditor.commit();
        }

    }

    private void copyDefaultToInternalStorage(InputStream defaultXML, String fileName) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

            int read = 0;
            while ((read = defaultXML.read()) != -1) {
                outputStream.write(read);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error creating exercise.xml "
                    + e.getMessage());
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }

    @Override
    public void updateProgressBar(int value) {
        progressBar.setProgress(value);
    }

    @Override
    public void showProgressBar(int value) {
        progressBar.setVisibility(value);
    }

    @Override
    public void displayExerciseList(ArrayList<ExerciseListItem> exerciseList) {
        ExerciseItemArrayAdapter exerciseItemArrayAdapter = new ExerciseItemArrayAdapter(R.layout.exercise_item, exerciseList);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewExerciseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(exerciseItemArrayAdapter);
    }

    @Override
    public void displayFilteredExerciseList(ArrayList<ExerciseListItem> exerciseList) {

    }


    @Override
    public void showExerciseEditor(Exercise exercise) {

    }
}

