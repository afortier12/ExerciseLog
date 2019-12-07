package wlu.cp670.fort7350_project.Presenter;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.test.annotation.UiThreadTest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;
import wlu.cp670.fort7350_project.Utils.ExerciseTarget;
import wlu.cp670.fort7350_project.Utils.ExerciseType;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)


public class ExerciseListTest {

    ExerciseList exerciseList;
    Context targetContext;
    IExerciseList.View view;


    @Before
    public void setUp() throws Exception {

        targetContext = getInstrumentation().getTargetContext();
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
            public void updateExerciseList(ArrayList<ExerciseListItem> exerciseList) {

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
    public void loadDefaults_FileExists() throws IOException {
        File file = new File(targetContext.getFilesDir(), "exercises.xml");

        boolean newFile = false;
        if (!file.exists()) file.createNewFile();
        assertEquals(exerciseList.loadDefaults(targetContext), 0);

    }

    @Test
    public void loadDefaults_FileDoesNotExist() {
        File file = new File(targetContext.getFilesDir(), "exercises.xml");
        boolean deleteResult = file.delete();

        assertEquals(exerciseList.loadDefaults(targetContext), 0);

    }

    @Test
    public void readExerciseListFromFile() {
        exerciseList.readExerciseListFromFile();
        assertNotNull(exerciseList.getXmlParse());
        assertEquals(exerciseList.getAsyncTaskState(), AsyncTask.Status.RUNNING);
    }


    /**
     * <h1>XMLParserTest</h1>
     * Triggers the AsyncTask with the exercise.xml file
     * and tests if the ArrayList<ExerciseItems> has been
     * created
     * reference:
     *     https://mattias.niklewski.com/2012/11/strange_loops.html
     *
     * @author Adam Fortier
     * @version 1.0
     * @since 2019-11-12
     * @throws InterruptedException
     */
    @Test
    public void XMLParserTest() throws InterruptedException{
        assertNull(exerciseList.getExerciseList());
        final CountDownLatch signal = new CountDownLatch(1);
        final ExerciseList.XMLParse.Listener listener =
                new ExerciseList.XMLParse.Listener() {
                    @Override
                    public void onComplete(ArrayList<ExerciseListItem> result) {
                        assertNotNull(exerciseList.getExerciseList());
                        signal.countDown();
                    }
                };

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AsyncTask<String, Integer, ArrayList<ExerciseListItem>> xmlParser =
                        new ExerciseList.XMLParse(exerciseList, view, listener);
                xmlParser.execute(targetContext.getFilesDir().toString(), "exercises.xml");
            }
        });

        signal.await();

    }

    /**
     * <h1>filterExerciseList_FilterInList</h1>
     * Checks if filteredList contains only the
     *
     *
     * @author Adam Fortier
     * @version 1.0
     * @since 2019-11-12
     * @throws InterruptedException
     */
    @Test
    public void filterExerciseList_FilterInList() {

        ArrayList<ExerciseListItem> itemList = new ArrayList<ExerciseListItem>();
        ExerciseListItem item = new ExerciseListItem("Test Exercise",
                ExerciseTarget.BICEP.toString(), ExerciseType.BODYWEIGHT.toString());
        itemList.add(item);
        item = new ExerciseListItem("Test Exercise",
                ExerciseTarget.TRICEP.toString(), ExerciseType.MACHINE.toString());
        itemList.add(item);
        exerciseList.setExerciseList(itemList);
        //check filter by target
        exerciseList.filterExerciseList(ExerciseFilter.TARGET, ExerciseTarget.BICEP.toString());
        for (ExerciseListItem listItem: exerciseList.getFilteredList()){
            assertEquals(listItem.getExerciseTarget(), ExerciseTarget.BICEP.toString());
        }
        //check filter by type
        exerciseList.filterExerciseList(ExerciseFilter.TYPE, ExerciseType.MACHINE.toString());
        for (ExerciseListItem listItem: exerciseList.getFilteredList()){
            assertEquals(listItem.getExerciseType(), ExerciseType.MACHINE.toString());
        }


    }

    /**`    1
     * <h1>filterExerciseList_FilterNotInList</h1>
     * If list does not contain the filtered value,
     * check that list is empty
     *
     *
     * @author Adam Fortier
     * @version 1.0
     * @since 2019-11-12
     * @throws InterruptedException
     */
    @Test
    public void filterExerciseList_FilterNotInList(){

        ArrayList<ExerciseListItem> itemList = new ArrayList<ExerciseListItem>();
        ExerciseListItem item = new ExerciseListItem("Test Exercise",
                ExerciseTarget.BICEP.toString(), ExerciseType.BODYWEIGHT.toString());
        itemList.add(item);
        exerciseList.setExerciseList(itemList);
        exerciseList.filterExerciseList(ExerciseFilter.TARGET, "TEST");

        assertTrue(exerciseList.getFilteredList().isEmpty());
    }

    @Test
    public void clearExerciseList(){
        ArrayList<ExerciseListItem> itemList = new ArrayList<ExerciseListItem>();
        ExerciseListItem item = new ExerciseListItem("Test Exercise",
                ExerciseTarget.BICEP.toString(), ExerciseType.BODYWEIGHT.toString());
        itemList.add(item);
        exerciseList.setExerciseList(itemList);

        exerciseList.clearExerciseListFilter();

        assertEquals(exerciseList.getExerciseList(), exerciseList.getFilteredList());
    }
}