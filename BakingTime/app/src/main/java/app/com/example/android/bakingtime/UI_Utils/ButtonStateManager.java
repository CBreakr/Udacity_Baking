package app.com.example.android.bakingtime.UI_Utils;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;

import app.com.example.android.bakingtime.MainActivity;
import app.com.example.android.bakingtime.R;

public class ButtonStateManager {

    public static void fillButtonStates(
        Button firstButton,
        Button secondButton,
        Button thirdButton,
        ConstraintLayout buttonNavBar,
        StateManagement state,
        MainActivity activity)
    {
        if(!state.isInitialState()){
            buttonNavBar.setVisibility(View.VISIBLE);
            setButtonDisplay(firstButton, secondButton, thirdButton, state);
            setListeners(firstButton, secondButton, thirdButton, activity);
        }
        else{
            buttonNavBar.setVisibility(View.GONE);
        }
    }

    private static void setButtonDisplay(
            Button firstButton,
            Button secondButton,
            Button thirdButton,
            StateManagement state
        )
    {
        StateManagement.UserActionSubType navigation = state.getNavigationSubType();
        StateManagement.UserActionSubType detail = state.getDetailSubType();

        firstButton.setVisibility(View.VISIBLE);
        thirdButton.setVisibility(View.VISIBLE);

        if(navigation != null && detail != null){
            secondButton.setVisibility(View.GONE);
            fillTwoPanelButtons(firstButton, thirdButton, navigation, detail);
        }
        else{
            // OK, this get a little bit hairy
            secondButton.setVisibility(View.VISIBLE);
            fillSinglePanelButtons(firstButton, secondButton, thirdButton, navigation, detail);
        }
    }

    private static void fillTwoPanelButtons(
            Button firstButton,
            Button thirdButton,
            StateManagement.UserActionSubType navigation,
            StateManagement.UserActionSubType detail)
    {
        if(navigation == StateManagement.UserActionSubType.RECIPELIST){
            setButtonToStepList(firstButton);
        }
        else{
            setButtonToRecipeList(firstButton);
        }

        if(detail == StateManagement.UserActionSubType.INGREDIENTS){
            setButtonToIndividualStep(thirdButton);
        }
        else{
            setButtonToIngredients(thirdButton);
        }
    }

    private static void fillSinglePanelButtons(
            Button firstButton,
            Button secondButton,
            Button thirdButton,
            StateManagement.UserActionSubType navigation,
            StateManagement.UserActionSubType detail)
    {
        // one of them will be null
        if(navigation == null){
            setButtonToRecipeList(firstButton);
            setButtonToStepList(secondButton);

            if(detail == StateManagement.UserActionSubType.INGREDIENTS){
                setButtonToIndividualStep(thirdButton);
            }
            else{
                setButtonToIngredients(thirdButton);
            }
        }
        else{
            if(navigation == StateManagement.UserActionSubType.RECIPELIST){
                setButtonToStepList(firstButton);
            }
            else{
                setButtonToRecipeList(firstButton);
            }
            setButtonToIndividualStep(secondButton);
            setButtonToIngredients(thirdButton);
        }
    }

    private static void setListeners(
            Button firstButton,
            Button secondButton,
            Button thirdButton,
            MainActivity activity)
    {
        setButtonClickListener(firstButton, activity);
        setButtonClickListener(secondButton, activity);
        setButtonClickListener(thirdButton, activity);
    }

    private static void setButtonToRecipeList(Button button){
        button.setText(R.string.RecipeSelectionLabel);
        button.setTag(StateManagement.UserAction.RECIPE_LIST);
    }

    private static void setButtonToStepList(Button button){
        button.setText(R.string.StepSelectionLabel);
        button.setTag(StateManagement.UserAction.STEP_LIST);
    }

    private static void setButtonToIndividualStep(Button button){
        button.setText(R.string.ReturnToStepLabel);
        button.setTag(StateManagement.UserAction.INDIVIDUAL_STEP);
    }

    private static void setButtonToIngredients(Button button){
        button.setText(R.string.IngredientViewLabel);
        button.setTag(StateManagement.UserAction.INGREDIENT_LIST);
    }

    private static void setButtonClickListener(Button button, final MainActivity activity){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.performButtonClick(v);
            }
        });
    }
}
