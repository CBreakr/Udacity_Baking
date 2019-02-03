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

import app.com.example.android.bakingtime.Adapters.StepAdapter;
import app.com.example.android.bakingtime.RecipeUtils.RawJsonReader;
import app.com.example.android.bakingtime.RecipeUtils.Step;
import app.com.example.android.bakingtime.UI_Utils.StateManagement;
import app.com.example.android.bakingtime.UI_Utils.SubState;

public class StepListFragment extends StateParameterFragment
        implements StepAdapter.StepAdapterClickHandler
{
    private List<Step> mStepList;

    private SubState mStepListSubState;

    private StepAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private static final int fTabletGridColumns = 1;
    private static final int fVerticalGridColumns = 2;
    private static final int fHorizontalGridColumns = 3;

    private static final String fStepKey = "steps";
    private static final String fSubStateKey = "substate";

    private static final String fBackstackTag = "STEPLIST";

    private MainActivity mParentActivity;

    public String getBackStackTag(){
        return fBackstackTag;
    }

    public void setSteps(List<Step> stepList){
        mStepList = stepList;
        if(mAdapter != null) {
            mAdapter.setStepData(mStepList);
        }
    }

    public void fillParameters(StateManagement state){
        mStepListSubState = state.getSubState().copy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerview_step_list);

        mParentActivity = (MainActivity) getActivity();

        if(savedInstanceState != null){
            setSteps(
                    RawJsonReader.parseStepsFromString(
                            savedInstanceState.getString(fStepKey)
                    )
            );

            mStepListSubState = savedInstanceState.getParcelable(fSubStateKey);
        }

        switch(mStepListSubState.getScreen()){
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
        mAdapter = new StepAdapter(this, mStepListSubState.getStepIndex());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        if(mStepList != null){
            mAdapter.setStepData(mStepList);
        }
    }

    private void updateParent(){
        MainActivity parent = (MainActivity) getActivity();
        parent.updateFromCurrentFragment(mStepListSubState);
    }

    @Override
    public void onClick(int index) {
        MainActivity parent = (MainActivity) getActivity();
        parent.processUserAction(StateManagement.UserAction.INDIVIDUAL_STEP, index);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(fStepKey, RawJsonReader.createJSONStringFromSteps(mStepList));
        outState.putParcelable(fSubStateKey, mStepListSubState);
    }
}
