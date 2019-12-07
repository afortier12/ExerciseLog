package wlu.cp670.fort7350_project.Views;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import wlu.cp670.fort7350_project.Presenter.ExerciseData;
import wlu.cp670.fort7350_project.Presenter.IExerciseData;
import wlu.cp670.fort7350_project.R;

public class SummaryActivity extends AppCompatActivity implements IExerciseData.View.Summary {

    public static final String TAG = "SummaryActivity";
    public static final String COLUMN_DATE = "timestamp";
    public static final String COLUMN_TARGET ="target";
    public static final String COLUMN_TOTAL_WEIGHT = "total_weight";
    public static final String COLUMN_TOTAL_REPS = "total_reps";

    ListView listView;
    Toolbar appToolbar;
    CursorAdapter summaryCursorAdapter;
    Cursor cursor;
    ExerciseData exerciseDataSummaryPresenter;

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

        } else if (id == R.id.action_history){

        } else {

        }
        return super.onOptionsItemSelected(menuItem);
    }

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

        appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);

        exerciseDataSummaryPresenter = new ExerciseData(this, this.getApplicationContext());
        exerciseDataSummaryPresenter.getSummaryData();
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
    public void displaySummaryData(Cursor cursor) {
        summaryCursorAdapter = new SummaryCursorAdapter(this, cursor, 0);
        listView.setAdapter(summaryCursorAdapter);
    }
}
