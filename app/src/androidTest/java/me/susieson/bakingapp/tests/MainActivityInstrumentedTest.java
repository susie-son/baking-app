package me.susieson.bakingapp.tests;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.susieson.bakingapp.R;
import me.susieson.bakingapp.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static me.susieson.bakingapp.utils.TestUtils.atPosition;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void openMainActivity_DisplaysMainFragment() {
        onView(ViewMatchers.withId(R.id.fragment_recipe_main_container))
                .check(matches(isDisplayed()));
    }

    @Test
    public void openMainActivity_DisplaysRecipeInfo() {
        onView(withId(R.id.recipe_main_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("Nutella Pie")))));

        onView(withId(R.id.recipe_main_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withId(R.id.item_recipe_image_view)))));

        onView(withId(R.id.recipe_main_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withId(R.id.item_recipe_serving_image_view)))));

        onView(withId(R.id.recipe_main_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("8")))));
    }

    @Test
    public void clickRecipeItem_OpensDetailActivity() {
        onView(withId(R.id.recipe_main_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.fragment_recipe_detail_container))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickBackButton_OpensHomeScreen() {
        Espresso.pressBackUnconditionally();

        assertTrue(mMainActivityTestRule.getActivity().isFinishing());
    }

}
