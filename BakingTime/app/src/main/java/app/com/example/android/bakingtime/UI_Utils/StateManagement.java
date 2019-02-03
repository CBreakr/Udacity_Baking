package app.com.example.android.bakingtime.UI_Utils;

import android.content.Context;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;

import app.com.example.android.bakingtime.IndividualStepFragment;
import app.com.example.android.bakingtime.IngredientFragment;
import app.com.example.android.bakingtime.R;
import app.com.example.android.bakingtime.RecipeListFragment;
import app.com.example.android.bakingtime.RecipeUtils.RawJsonReader;
import app.com.example.android.bakingtime.RecipeUtils.Recipe;
import app.com.example.android.bakingtime.StateParameterFragment;
import app.com.example.android.bakingtime.StepListFragment;

public class StateManagement implements Parcelable {

    public static final int NoPosition = -1;
    public static final int NoIndexChange = -2;

    public static final int single_pane_target = R.id.content_frame;
    public static final int navigation_target = R.id.navigation_frame;
    public static final int detail_target = R.id.detail_frame;

    private List<Recipe> mRecipeList;

    private SubState mSubState;

    private boolean mMoveStepForward = false;

    /*
    enum to and from int
    https://stackoverflow.com/questions/6220842/setting-an-enum-by-using-an-int-specific-to-that-enum
    from user: Bohemian
     */
    public enum UserAction {
        INITIAL(0),
        RECIPE_LIST(1),
        STEP_LIST(2),
        INGREDIENT_LIST(3),
        INDIVIDUAL_STEP(4),
        NEXT_STEP(5),
        PREVIOUS_STEP(6),
        SELECT_RECIPE(7);

        private final int value;
        UserAction(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static UserAction forInt(int id) {
            for (UserAction action : values()) {
                if (action.value == id) {
                    return action;
                }
            }
            throw new IllegalArgumentException("Invalid Day id: " + id);
        }

        public UserActionType getActionType(){
            if(value == INITIAL.getValue()
                    || value == RECIPE_LIST.getValue()
                    || value == STEP_LIST.getValue())
            {
                return UserActionType.NAVIGATION;
            }
            else{
                return UserActionType.DETAIL;
            }
        }

        public UserActionSubType getActionSubType(){
            if(value == INITIAL.getValue()
                    || value == RECIPE_LIST.getValue())
            {
                return UserActionSubType.RECIPELIST;
            }
            else if(value == STEP_LIST.getValue()){
                return UserActionSubType.STEPLIST;
            }
            else if(value == INGREDIENT_LIST.getValue()){
                return UserActionSubType.INGREDIENTS;
            }
            else{
                return UserActionSubType.INDIVIDUALSTEP;
            }
        }
    }

    public enum UserActionType{
        NAVIGATION(0),
        DETAIL(1);

        private final int value;
        UserActionType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static UserActionType forInt(int id) {
            for (UserActionType actionType : values()) {
                if (actionType.value == id) {
                    return actionType;
                }
            }
            throw new IllegalArgumentException("Invalid Day id: " + id);
        }
    }

    public enum UserActionSubType{
        RECIPELIST(0),
        STEPLIST(1),
        INGREDIENTS(2),
        INDIVIDUALSTEP(3);

        private final int value;
        UserActionSubType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static UserActionSubType forInt(int id) {
            for (UserActionSubType actionSubType : values()) {
                if (actionSubType.value == id) {
                    return actionSubType;
                }
            }
            throw new IllegalArgumentException("Invalid Day id: " + id);
        }

        public UserActionType getParentType(){
            if(value == RECIPELIST.getValue() || value== STEPLIST.getValue()){
                return UserActionType.NAVIGATION;
            }
            else{
                return UserActionType.DETAIL;
            }
        }
    }

    public enum ScreenMode{
        PORTRAIT(1),
        LANDSCAPE(2),
        TABLET(3);

        private final int value;
        ScreenMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ScreenMode forInt(int id) {
            for (ScreenMode mode : values()) {
                if (mode.value == id) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("Invalid Day id: " + id);
        }
    }

    public StateManagement(List<Recipe> list){
        mRecipeList = list;
        mSubState = new SubState();
        mSubState.setRecipeIndex(NoPosition);
        mSubState.setStepIndex(NoPosition);
    }

    public boolean isInitialState(){
        if(mSubState.getRecipeIndex() == NoPosition){
            return true;
        }
        return false;
    }

    public UserActionSubType getNavigationSubType(){
        return mSubState.getCurrentNavigationActionSubType();
    }

    public UserActionSubType getDetailSubType(){
        return mSubState.getCurrentDetailActionSubType();
    }

    public void setScreen(String layoutTag, Context context){
        mSubState.setScreen(layoutTag, context);
    }

    public ScreenMode getScreen() {
        return mSubState.getScreen();
    }

    public void setSubState(SubState subState){
        mSubState = subState.copy();
    }

    public SubState getSubState(){
        return mSubState;
    }

    public Recipe getCurrentRecipe(){
        int index = mSubState.getRecipeIndex();
        if(index != StateManagement.NoPosition){
            return mRecipeList.get(index);
        }
        return null;
    }

    /*
    Called by the MainActivity to handle the user action
     */
    public List<FragmentMovement> transitionFragmentsBasedOnState(
            UserAction action,
            int index,
            Context context)
    {
        updateIndexes(action, index);
        List<FragmentMovement> fragments = determineTransitions(action, index);
        updateCurrentActions(action);
        return fragments;
    }

    private List<FragmentMovement> determineTransitions(UserAction action, int index){

        List<FragmentMovement> transitions = new ArrayList<>();

        switch(action){
            case INITIAL:
                transitions.add(transitionINITIAL());
                break;
            case RECIPE_LIST:
                transitions.add(transitionRECIPE_LIST());
                break;
            case STEP_LIST:
                transitions.add((transitionSTEP_LIST()));
                break;
            case INGREDIENT_LIST:
                transitions.add(transitionINGREDIENT_LIST());
                break;
            case INDIVIDUAL_STEP:
                transitions.add(transitionINDIVIDUAL_STEP(false));
                break;
            case NEXT_STEP:
                transitions.add(transitionNEXT_STEP());
                break;
            case PREVIOUS_STEP:
                transitions.add(transitionPREVIOUS_STEP());
                break;
            case SELECT_RECIPE:
                transitions.addAll(transitionSELECT_RECIPE(index));
                break;
        }

        return transitions;
    }

    private FragmentMovement transitionINITIAL(){
        return transitionRECIPE_LIST();
    }

    private FragmentMovement transitionRECIPE_LIST(){
        int target;

        if(mSubState.getScreen() == ScreenMode.TABLET){
            target = navigation_target;
        }
        else{
            target = single_pane_target;
        }

        FragmentMovement.TransitionAnimation transition;

        if(mSubState.getCurrentNavigationActionSubType() == null){
            transition = FragmentMovement.TransitionAnimation.TOP;
        }
        else{
            transition = FragmentMovement.TransitionAnimation.LEFT;
        }

        RecipeListFragment fragment = new RecipeListFragment();
        fragment.setRecipes(mRecipeList);

        return createIndividualMovement(
                target,
                transition,
                fragment,
                UserAction.RECIPE_LIST);
    }

    private FragmentMovement transitionSTEP_LIST(){
        int target;

        if(mSubState.getScreen() == ScreenMode.TABLET){
            target = navigation_target;
        }
        else{
            target = single_pane_target;
        }

        FragmentMovement.TransitionAnimation transition;

        if(mSubState.getCurrentNavigationActionSubType() == null){
            transition = FragmentMovement.TransitionAnimation.TOP;
        }
        else{
            transition = FragmentMovement.TransitionAnimation.RIGHT;
        }

        StepListFragment fragment = new StepListFragment();
        fragment.setSteps(getCurrentRecipe().getSteps());

        return createIndividualMovement(
                target,
                transition,
                fragment,
                UserAction.STEP_LIST);
    }

    private FragmentMovement transitionINGREDIENT_LIST(){
        int target;

        if(mSubState.getScreen() == ScreenMode.TABLET){
            target = detail_target;
        }
        else{
            target = single_pane_target;
        }

        IngredientFragment fragment = new IngredientFragment();
        fragment.setIngredients(getCurrentRecipe().getIngredients());

        FragmentMovement.TransitionAnimation transition;

        if(mSubState.getCurrentDetailActionSubType() == null){
            transition = FragmentMovement.TransitionAnimation.BOTTOM;
        }
        else{
            transition = FragmentMovement.TransitionAnimation.LEFT;
        }

        return createIndividualMovement(
                target,
                transition,
                fragment,
                UserAction.INGREDIENT_LIST);
    }

    private FragmentMovement transitionINDIVIDUAL_STEP(boolean initial){
        int target;

        // hmm... OK, this one is actually weirder...
        //
        // what I have here is for if the user is on the detail level already
        //
        // if they're coming from the recipe or step list then it needs to be a vertical change
        // ---> only for the single-pane version
        //
        // so if the previous detail action was null
        //

        if(mSubState.getScreen() == ScreenMode.TABLET){
            target = detail_target;
        }
        else{
            target = single_pane_target;
        }

        FragmentMovement.TransitionAnimation transition;

        if(mSubState.getCurrentDetailActionSubType() == null){
            if(initial){
                transition = FragmentMovement.TransitionAnimation.TOP;
            }
            else {
                transition = FragmentMovement.TransitionAnimation.BOTTOM;
            }
        }
        else {
            if(mMoveStepForward){
                transition = FragmentMovement.TransitionAnimation.RIGHT;
            }
            else{
                transition = FragmentMovement.TransitionAnimation.LEFT;
            }
        }

        IndividualStepFragment fragment = new IndividualStepFragment();

        return createIndividualMovement(
                target,
                transition,
                fragment,
                UserAction.INDIVIDUAL_STEP);
    }

    private FragmentMovement transitionNEXT_STEP(){

        int target;

        if(mSubState.getScreen() == ScreenMode.TABLET){
            target = detail_target;
        }
        else{
            target = single_pane_target;
        }

        IndividualStepFragment fragment = new IndividualStepFragment();

        return createIndividualMovement(
                target,
                FragmentMovement.TransitionAnimation.RIGHT,
                fragment,
                UserAction.NEXT_STEP);
    }

    private FragmentMovement transitionPREVIOUS_STEP(){
        int target;

        if(mSubState.getScreen() == ScreenMode.TABLET){
            target = detail_target;
        }
        else{
            target = single_pane_target;
        }

        IndividualStepFragment fragment = new IndividualStepFragment();

        return createIndividualMovement(
                target,
                FragmentMovement.TransitionAnimation.LEFT,
                fragment,
                UserAction.PREVIOUS_STEP);
    }

    private List<FragmentMovement> transitionSELECT_RECIPE(int index){
        List<FragmentMovement> transitions = new ArrayList<>();

        // the move to the step list
        // the move to the first step

        transitions.add(transitionSTEP_LIST());
        transitions.add(transitionINDIVIDUAL_STEP(true));

        return transitions;
    }

    private FragmentMovement createIndividualMovement(
            int target,
            FragmentMovement.TransitionAnimation anim,
            StateParameterFragment fragment,
            UserAction action
        )
    {
        updateCurrentActions(action);
        fragment.fillParameters(this);

        return new FragmentMovement(
                target,
                anim,
                fragment,
                fragment.getBackStackTag());
    }

    public void updateIndexes(
            UserAction action,
            int index)
    {
        switch(action){
            case RECIPE_LIST:
            case STEP_LIST:
            case INGREDIENT_LIST:
                break;
            case NEXT_STEP:
            case PREVIOUS_STEP:
            case INDIVIDUAL_STEP:
                if(index != NoIndexChange) {
                    if(index > mSubState.getStepIndex()) {
                        mMoveStepForward = true;
                    }
                    mSubState.setStepIndex(index);
                }
                break;
            case SELECT_RECIPE:
                if(index != NoIndexChange) {
                    mSubState.setRecipeIndex(index);
                }
                mSubState.setStepIndex(0);
                break;
        }
    }

    public void updateCurrentActions(UserAction action){
        UserActionType actionType = action.getActionType();
        if(actionType == UserActionType.NAVIGATION){
            mSubState.setCurrentNavigationActionSubType(action.getActionSubType());
            if(mSubState.getScreen() != ScreenMode.TABLET){
                mSubState.setCurrentDetailActionSubType(null);
            }
        }
        else{
            mSubState.setCurrentDetailActionSubType(action.getActionSubType());
            if(mSubState.getScreen() != ScreenMode.TABLET){
                mSubState.setCurrentNavigationActionSubType(null);
            }
        }

        mMoveStepForward = false;
    }

    //
    // PARCELABLE
    //

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public StateManagement createFromParcel(Parcel in) {
            return new StateManagement(in);
        }

        public StateManagement[] newArray(int size) {
            return new StateManagement[size];
        }
    };

    public StateManagement(Parcel in){
        this.mRecipeList = RawJsonReader.parseRecipesFromString(in.readString());
        mSubState = in.readParcelable(SubState.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(RawJsonReader.createJSONStringFromRecipes(this.mRecipeList));
        out.writeParcelable(mSubState, flags);
    }
}
