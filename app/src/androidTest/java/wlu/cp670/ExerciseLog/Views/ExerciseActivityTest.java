package wlu.cp670.ExerciseLog.Views;

import android.content.Context;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import wlu.cp670.ExerciseLog.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

//references used:
// https://blog.tinkercademy.com/how-i-wrote-ui-tests-for-the-get-hacking-android-app-7111309adeb9

@RunWith(AndroidJUnit4.class)
public class ExerciseActivityTest {

    @Rule
    public final ActivityScenarioRule<ExerciseActivity> scenarioRule
            = new ActivityScenarioRule<>(ExerciseActivity.class);
    @Before
    public void setUp() throws Exception {
        Context context = getInstrumentation().getTargetContext();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onCreate() {
    }


    @Test
    public void onLaunch_displayAllViews() {
        onView(withId(R.id.recyclerViewExerciseList)).check(matches(isDisplayed()));
        onView(withId(R.id.app_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.progressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

    }
}