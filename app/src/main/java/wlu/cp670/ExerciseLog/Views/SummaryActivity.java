package wlu.cp670.ExerciseLog.Views;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import wlu.cp670.ExerciseLog.Presenter.ExerciseData;
import wlu.cp670.ExerciseLog.Presenter.IExerciseData;
import wlu.cp670.ExerciseLog.R;


/**
 * Class SummaryActivity
 *
 * layout: activity_main.xml
 * @see wlu.cp670.ExerciseLog.Presenter.IExerciseData.View.Summary
 * @author fort7350
 * @version 1.0
 */
public class SummaryActivity extends AppCompatActivity implements IExerciseData.View.Summary {

    public static final String TAG = "SummaryActivity";
    public static final String COLUMN_DATE = "timestamp";
    public static final String COLUMN_TARGET ="target";
    public static final String COLUMN_EXERCISE = "name";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_MAX_WEIGHT = "max_weight";
    public static final String COLUMN_MIN_WEIGHT = "min_weight";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_MAX_REPS = "max_reps";
    public static final String COLUMN_MIN_REPS = "min_reps";
    public static final int WRITE_REQUEST_CODE = 7;

    ListView listView;
    Toolbar appToolbar;
    CursorAdapter summaryCursorAdapter;
    ExerciseData exerciseDataSummaryPresenter;

    boolean showList = true;
    boolean showFilter = false;
    boolean showHistory = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.summaryList);
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
                new int[] {R.color.colorBody, R.color.colorBody}));
        listView.setDividerHeight(1);
        LayoutInflater headerInflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) headerInflater.inflate(R.layout.summary_row_header,
                listView, false);
        listView.addHeaderView(header);

        appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        invalidateOptionsMenu();

        exerciseDataSummaryPresenter = new ExerciseData(this, this.getApplicationContext());
        exerciseDataSummaryPresenter.getSummaryData();

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_list) {
            Log.d(TAG, "list selected");

            Intent intent = new Intent(this, ExerciseActivity.class);
            startActivity(intent);


        } else if (id == R.id.action_tools) {

            Intent intent = new Intent(this, ImportExportActivity.class);
            startActivity(intent);

        } else if (id == R.id.action_history){

        } else if (id == R.id.action_about){
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

    @Override
    public void displaySummaryData(Cursor cursor) {
        summaryCursorAdapter = new SummaryCursorAdapter(this, cursor, 0);
        listView.setAdapter(summaryCursorAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if(grantResults[0] == getPackageManager().PERMISSION_GRANTED){
                    //Granted.


                }
                else{
                    //Denied.
                }
                break;
        }
    }


    /**
     * This method is called from the onClick setting in the Button resource file
     *
     * Starts the ExerciseActivity
     *
     * @param view
     * @author fort7350
     * @version 1.0
     */
    public void onStartWorkout(View view) {
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }
}
