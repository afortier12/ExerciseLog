package wlu.cp670.fort7350_project.Presenter;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)

public class ExerciseListTest {
    SharedPreferences.Editor preferencesEditor;
    ExerciseList exerciseList;
    Context targetContext;
    IExerciseList.View view;

//    @Rule
//    public ActivityTestRule<ExerciseActivity> mActivityRule = new ActivityTestRule<>(
//            ExerciseActivity.class,
//            true,
//            false); // Activity is not launched immediately

    @Before
    public void setUp() throws Exception {
        targetContext = getInstrumentation().getTargetContext();
        //preferencesEditor = PreferenceManager.getDefaultSharedPreferences(targetContext).edit();
        //preferencesEditor.remove(targetContext.getString(R.string.preference_key_default_loaded));

        view = new IExerciseList.View() {
            @Override
            public void updateProgressBar(int value) {

            }

            @Override
            public void showProgressBar(int value) {

            }

            @Override
            public void displayExerciseList(ArrayList<ExerciseListItem> exerciseList) {

            }

            @Override
            public void displayFilteredExerciseList(ArrayList<ExerciseListItem> exerciseList) {

            }

            @Override
            public void showExerciseEditor(Exercise exercise) {

            }
        };

        exerciseList = new ExerciseList("exercise.xml", view);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void constructorTest(){
        assertEquals(exerciseList.getXmlSource(), "exercise.xml");
        assertThat(exerciseList.getView(), instanceOf(IExerciseList.View.class));
    }

    @Test
    public void loadDefaults_FileExists() {
        assertEquals(exerciseList.loadDefaults(targetContext), 0);
    }

    @Test
    public void loadDefaults_FileDoesNotExist() {
        boolean deleteResult = targetContext.deleteFile("exercise.xml");

        assertEquals(exerciseList.loadDefaults(targetContext), 0);

    }

    @Test
    public void openExerciseEditor() {
    }

    @Test
    public void updateExerciseList() {
    }

    @Test
    public void stop() {
    }

    @Test
    public void getExerciseList() {
    }

    @Test
    public void filterExerciseList() {
    }


}