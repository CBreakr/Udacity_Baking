package app.com.example.android.bakingtime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import app.com.example.android.bakingtime.Adapters.IngredientAdapter;
import app.com.example.android.bakingtime.RecipeUtils.Ingredient;
import app.com.example.android.bakingtime.RecipeUtils.RawJsonReader;
import app.com.example.android.bakingtime.UI_Utils.StateManagement;
import app.com.example.android.bakingtime.UI_Utils.SubState;

public class IngredientFragment extends StateParameterFragment {

    private List<Ingredient> mIngredientList;

    private SubState mIngredientSubState;

    private IngredientAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private static final int fTabletGridColumns = 1;
    private static final int fVerticalGridColumns = 2;
    private static final int fHorizontalGridColumns = 3;

    private static final String fIngredientsKey = "ingredients";
    private static final String fSubStateKey = "substate";

    private static final String fBackstackTag = "INGREDIENTS";

    private MainActivity mParentActivity;

    public String getBackStackTag(){
        return fBackstackTag;
    }

    public void setIngredients(List<Ingredient> ingredientList){
        mIngredientList = ingredientList;
        if(mAdapter != null) {
            mAdapter.setIngredientData(mIngredientList);
        }
    }

    public void fillParameters(StateManagement state){
        mIngredientSubState = state.getSubState().copy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView;
        rootView = inflater.inflate(R.layout.ingredient_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerview_ingredient_list);

        mParentActivity = (MainActivity) getActivity();

        if(savedInstanceState != null){
            setIngredients(
                    RawJsonReader.parseIngredientsFromString(
                            savedInstanceState.getString(fIngredientsKey)
                    )
            );

            mIngredientSubState = savedInstanceState.getParcelable(fSubStateKey);
        }

        switch(mIngredientSubState.getScreen()){
            case PORTRAIT:
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), fVerticalGridColumns));
                break;
            case LANDSCAPE:
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), fHorizontalGridColumns));
                break;
            case TABLET:
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), fTabletGridColumns));
                break;
        }

        setupAdapter();

        updateParent();

        return rootView;
    }

    protected void setupAdapter(){
        mAdapter = new IngredientAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        if(mIngredientList != null){
            mAdapter.setIngredientData(mIngredientList);
        }
    }

    private void updateParent(){
        MainActivity parent = (MainActivity) getActivity();
        parent.updateFromCurrentFragment(mIngredientSubState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(fIngredientsKey, RawJsonReader.createJSONStringFromIngredients(mIngredientList));
        outState.putParcelable(fSubStateKey, mIngredientSubState);
    }
}
