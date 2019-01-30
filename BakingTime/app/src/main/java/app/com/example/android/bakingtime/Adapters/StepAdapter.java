package app.com.example.android.bakingtime.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.com.example.android.bakingtime.R;

//import butterknife.BindView;
//import butterknife.ButterKnife;

import java.util.List;

import app.com.example.android.bakingtime.RecipeUtils.Step;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder> {

    private List<Step> mStepData;
    private int mCurrentStepIndex;

    private static final int SELECTED = 100;
    private static final int UNSELECTED = 200;

    private final StepAdapterClickHandler mClickHandler;

    public interface StepAdapterClickHandler{
        void onClick(int index);
    }

    public StepAdapter(StepAdapterClickHandler clickHandler, int currentStepIndex) {
        mClickHandler = clickHandler;
        mCurrentStepIndex = currentStepIndex;
    }

    public void setStepData(List<Step> steps) {
        mStepData = steps;
        notifyDataSetChanged();
    }

    /*
    OVERRIDE METHODS
     */

    @Override
    public int getItemViewType(int position) {
        if(position == mCurrentStepIndex){
            return SELECTED;
        }
        return UNSELECTED;
    }

    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem;

//        viewType == SELECTED
        layoutIdForListItem = R.layout.step_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new StepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepAdapterViewHolder stepAdapterViewHolder, int position) {
        Step currentStep = mStepData.get(position);

        String display = String.valueOf(position) + ". " + currentStep.getShortDescription();
        stepAdapterViewHolder.mStepDescription.setText(display);
    }

    @Override
    public int getItemCount() {
        if (null == mStepData) return 0;
        return mStepData.size();
    }

    /*
    VIEWHOLDER
     */

    public class StepAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
//        @BindView(R.id.step_description) TextView mStepDescription;

        public TextView mStepDescription;

        public StepAdapterViewHolder(View view) {
            super(view);
//            ButterKnife.bind(view);

            mStepDescription = view.findViewById(R.id.step_description);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position);
        }
    }
}