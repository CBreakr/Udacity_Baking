package app.com.example.android.bakingtime;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.support.test.espresso.intent.Checks.checkNotNull;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RecipeDisplayTest {

    private static final int recipeIndex = 1;
    private static final int stepIndex = 0;
    private static final int ingredientIndex = 0;
    private static final String RECIPE_NAME = "Brownies";
    private static final String RECIPE_INTRO_TEXT = "Recipe Introduction";
    private static final String RECIPE_NEXT_STEP_TEXT = "Preheat the oven";
    private static final String INGREDIENT_TEXT = "Bittersweet chocolate";


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry registry = IdlingRegistry.getInstance();
        registry.register(mIdlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        if(mIdlingResource != null){
            IdlingRegistry registry = IdlingRegistry.getInstance();
            registry.unregister(mIdlingResource);
        }
    }

    /*
    all actions:
X    initial load
X    click recipe
    click next step
    click previous step
    click button for recipes
    click button for ingredients
    click button for steps
    click step
XXX
    back button: run a sequence of checks: 9 (same checks + 1 for initial recipe click + 1 for testing the final backstack)
     */

    @Test
    public void fullLoopOfApplication() {

        // check the initial load
        checkRecipeList();

        clickRecipe();
        checkIntroStep();

        // a series of steps which will then be reversed

        // and now the chiasmus
        runSymmetricTests();
        // the chiasmus now ends

        // the initial and final steps are slightly non-symmetric
        // due to two actions being performed at once

        pressBack();
        checkIntroStep();

        pressBack();
        checkStepList();

        // and we're back to symmetry
        pressBack();
        checkRecipeList();

        // make sure we stay there on the last backstack entry
        pressBack();
        checkRecipeList();
    }

    private void runSymmetricTests(){
/*
    click next step
    click previous step
    click button for ingredients
    click button for steps
    click step
    click button for recipes
 */

        clickNextStep();
        checkNextStep();

        clickPreviousStep();
        checkPreviousStep();

        clickIngredientListButton();
        checkIngredientList();

        clickStepListButton();
        checkStepList();

        clickFirstStep();
        checkIntroStep();

        // centerpoint
        clickRecipeListButton();
        checkRecipeList();

        //
        // reverse
        //

        pressBack();
        checkIntroStep();

        pressBack();
        checkStepList();

        pressBack();
        checkIngredientList();

        pressBack();
        checkPreviousStep();

        pressBack();
        checkNextStep();
    }

    //
    // CLICKS
    //

    private void pressBack(){
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    private void clickRecipe(){
        onView(withId(R.id.recyclerview_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(recipeIndex, click()));
    }

    private void clickNextStep(){
        onView(withId(R.id.next_step_button)).perform(click());
    }

    private void clickPreviousStep(){
        onView(withId(R.id.previous_step_button)).perform(click());
    }

    private void clickIngredientListButton(){
        onView(withId(R.id.detail_button)).perform(click());
    }

    private void clickStepListButton(){
        onView(withId(R.id.navigation_button_2)).perform(click());
    }

    private void clickFirstStep(){
        onView(withId(R.id.recyclerview_step_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(stepIndex, click()));
    }

    private void clickRecipeListButton(){
        onView(withId(R.id.navigation_button_1)).perform(click());
    }

    //
    // CHECKS
    //

    private void checkRecipeList(){
        onView(withId(R.id.recyclerview_recipe_list))
                .check(matches(atPosition(recipeIndex, withText(RECIPE_NAME), R.id.recipe_name)));
    }

    private void checkIntroStep(){
        onView(withId(R.id.recipe_step_text))
                .check(matches(hasValueContaining(RECIPE_INTRO_TEXT)));
    }

    private void checkNextStep(){
        onView(withId(R.id.recipe_step_text))
                .check(matches(hasValueContaining(RECIPE_NEXT_STEP_TEXT)));
        //hasValueContaining(RECIPE_NEXT_STEP_TEXT)
    }

    private void checkPreviousStep(){
        checkIntroStep();
    }

    private void checkIngredientList(){
        onView(withId(R.id.recyclerview_ingredient_list))
                .check(matches(
                        atPosition(ingredientIndex
                        , hasValueContaining(INGREDIENT_TEXT), R.id.ingredient_name)));
    }

    private void checkStepList(){
        onView(withId(R.id.recyclerview_step_list))
                .check(matches(
                        atPosition(stepIndex
                                , hasValueContaining(RECIPE_INTRO_TEXT), R.id.step_description)));
    }

    //
    // custom matchers
    //

    // taken from: riwnodennyk
    // https://stackoverflow.com/questions/31394569/how-to-assert-inside-a-recyclerview-in-espresso
    public static Matcher<View> atPosition(
            @NonNull final int position,
            @NonNull final Matcher<View> itemMatcher,
            @NonNull final int innerViewId)
    {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                View v = viewHolder.itemView.findViewById(innerViewId);
                if(v == null){
                    return false;
                }
                return itemMatcher.matches(v);
            }
        };
    }

    //
    // for some reason, the containsString matcher doesn't always seem to work
    //
    // taken from: jeprubio
    // https://stackoverflow.com/questions/45597008/espresso-get-text-of-element
    private Matcher<View> hasValueContaining(final String content) {

        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Has EditText/TextView the value:  " + content);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextView) && !(view instanceof EditText)) {
                    return false;
                }
                if (view != null) {
                    String text;
                    if (view instanceof TextView) {
                        text = ((TextView) view).getText().toString();
                    } else {
                        text = ((EditText) view).getText().toString();
                    }

                    return (text.contains(content));
                }
                return false;
            }
        };
    }
}
