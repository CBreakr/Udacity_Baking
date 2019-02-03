package app.com.example.android.bakingtime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.com.example.android.bakingtime.Adapters.RecipeAdapter;
import app.com.example.android.bakingtime.RecipeUtils.RawJsonReader;
import app.com.example.android.bakingtime.RecipeUtils.Recipe;
import app.com.example.android.bakingtime.UI_Utils.StateManagement;
import app.com.example.android.bakingtime.UI_Utils.SubState;

public class RecipeListFragment extends StateParameterFragment
        implements RecipeAdapter.RecipeAdapterClickHandler
{
    private List<Recipe> mRecipeList;

    private SubState mRecipeListSubState;

    private RecipeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private static final int fVerticalGridColumns = 1;
    private static final int fHorizontalGridColumns = 2;

    private static final String fRecipeKey = "recipes";
    private static final String fSubStateKey = "substate";

    private static final String fBackstackTag = "RECIPELIST";

    private MainActivity mParentActivity;

    public String getBackStackTag(){
        return fBackstackTag;
    }

    public void setRecipes(List<Recipe> recipeList){
        mRecipeList = recipeList;
        if(mAdapter != null) {
            mAdapter.setRecipeData(mRecipeList);
        }
    }

    public void fillParameters(StateManagement state){
        mRecipeListSubState = state.getSubState().copy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerview_recipe_list);

        mParentActivity = (MainActivity) getActivity();

        if(savedInstanceState != null){
            setRecipes(
                    RawJsonReader.parseRecipesFromString(
                            savedInstanceState.getString(fRecipeKey)
                    )
            );

            mRecipeListSubState = savedInstanceState.getParcelable(fSubStateKey);
        }

        if(mRecipeListSubState.isVertical(mParentActivity)){
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), fVerticalGridColumns));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), fHorizontalGridColumns));
        }

        setupAdapter();

        updateParent();

        return rootView;
    }

    protected void setupAdapter(){
        mAdapter = new RecipeAdapter(this, mRecipeListSubState.getRecipeIndex());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        if(mRecipeList != null){
            mAdapter.setRecipeData(mRecipeList);
        }
    }

    private void updateParent(){
        MainActivity parent = (MainActivity) getActivity();
        parent.updateFromCurrentFragment(mRecipeListSubState);
    }

    @Override
    public void onClick(int index) {
        MainActivity parent = (MainActivity) getActivity();
        parent.processUserAction(StateManagement.UserAction.SELECT_RECIPE, index);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(fRecipeKey, RawJsonReader.createJSONStringFromRecipes(mRecipeList));
        outState.putParcelable(fSubStateKey, mRecipeListSubState);
    }
}
