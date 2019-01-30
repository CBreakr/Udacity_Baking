package app.com.example.android.bakingtime;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import app.com.example.android.bakingtime.IdlingResource.SimpleIdlingResource;
import app.com.example.android.bakingtime.RecipeUtils.RawJsonReader;
import app.com.example.android.bakingtime.RecipeUtils.Recipe;
import app.com.example.android.bakingtime.Retrofit.RetroController;
import app.com.example.android.bakingtime.UI_Utils.ButtonStateManager;
import app.com.example.android.bakingtime.UI_Utils.FragmentMovement;
import app.com.example.android.bakingtime.UI_Utils.StateManagement;
import app.com.example.android.bakingtime.UI_Utils.SubState;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    // TODO - move numbers for dimensions to values

    // TODO - implement the exo-player for step

    private final String fStateManagementParcelableKey = "STATE_MANAGEMENT_KEY";
    private StateManagement mCurrentState;

    @BindView(R.id.navigation_button_1) Button mFirstButton;
    @BindView(R.id.navigation_button_2) Button mSecondButton;
    @BindView(R.id.detail_button) Button mThirdButton;

    @BindView(R.id.layout_tag_view) TextView layoutTagView;

    @BindView(R.id.button_navigation_layout) ConstraintLayout mButtonNavBar;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    public SimpleIdlingResource getIdlingResource() {
        if(mIdlingResource == null){
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    public void setIsIdle(){
        getIdlingResource().setIdleState(true);
    }

    public void setIsNotIdle(){
        getIdlingResource().setIdleState(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if(savedInstanceState != null){
            retrieveValuesFromSavedState(savedInstanceState);
            setCurrentStateScreenMode();
            setButtons();
        }
        else{
            RetroController retroController = new RetroController(this);
            retroController.start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable(fStateManagementParcelableKey, mCurrentState);
    }

    private void retrieveValuesFromSavedState(Bundle savedInstanceState){
        mCurrentState = savedInstanceState.getParcelable(fStateManagementParcelableKey);
    }

    private void fillInitialState(){
        List<Recipe> recipes = RawJsonReader.getRecipes(this);

        Gson gson = new Gson();
        String json = gson.toJson(recipes);

        List<Recipe> newRecipes = RawJsonReader.parseRecipesFromString(json);
        mCurrentState = new StateManagement(newRecipes);
    }

    public void completeInitialSetup(List<Recipe> recipes){
        mCurrentState = new StateManagement(recipes);
        setCurrentStateScreenMode();
        // and now set the initial fragments
        processUserAction(StateManagement.UserAction.INITIAL);
        setButtons();
    }

    private void setCurrentStateScreenMode(){
        String layoutTag = layoutTagView.getText().toString();
        mCurrentState.setScreen(layoutTag, this);
    }

    private void setButtons(){
        ButtonStateManager.fillButtonStates(
                mFirstButton,
                mSecondButton,
                mThirdButton,
                mButtonNavBar,
                mCurrentState,
                this);
    }

    /*
    gather these parameters from whatever interaction and pass them here
     */
    public void processUserAction(StateManagement.UserAction action){
        processUserAction(action, StateManagement.NoIndexChange);
    }

    public void processUserAction(StateManagement.UserAction action, int index){
        List<FragmentMovement> transitions = mCurrentState.transitionFragmentsBasedOnState(action, index, this);
        if(transitions != null){
            for(FragmentMovement fm : transitions){
                fm.replaceFragmentWithAnimation(this);
            }
        }
    }

    public void performButtonClick(View v){
        // get the action from the view
        // use the tag of the button clicked to do this

        StateManagement.UserAction action = (StateManagement.UserAction)v.getTag();
        processUserAction(action);
    }

    public void updateFromCurrentFragment(SubState mSubState){
        mCurrentState.setSubState(mSubState);
        setButtons();
    }

    // make sure we don't empty the activity from the initial fragment
    @Override
    public void onBackPressed() {

        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount <= 1) {
            // do nothing
        } else {
            super.onBackPressed();
        }
    }
}
