package wlu.cp670.fort7350_project.Views;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

//references used:
// https://blog.tinkercademy.com/how-i-wrote-ui-tests-for-the-get-hacking-android-app-7111309adeb9

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityScenarioRule<MainActivity> scenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onCreate() {
    }
}