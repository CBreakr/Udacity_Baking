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

import app.com.example.android.bakingtime.RecipeUtils.Ingredient;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientAdapterViewHolder> {

    private List<Ingredient> mIngredientData;

    public void setIngredientData(List<Ingredient> ingredients) {
        mIngredientData = ingredients;
        notifyDataSetChanged();
    }

    /*
    OVERRIDE METHODS
     */

    @Override
    public IngredientAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.ingredient_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new IngredientAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientAdapterViewHolder ingredientAdapterViewHolder, int position) {
        Ingredient currentIngredient = mIngredientData.get(position);

        ingredientAdapterViewHolder.mIngredientNameView.setText(currentIngredient.getIngredient());
        ingredientAdapterViewHolder.mIngredientMeasureView.setText(currentIngredient.getQuantity());
        ingredientAdapterViewHolder.mIngredientUnitView.setText(currentIngredient.getUnitOfMeasure().toLowerCase());
    }

    @Override
    public int getItemCount() {
        if (null == mIngredientData) return 0;
        return mIngredientData.size();
    }

    /*
    VIEWHOLDER
     */

    public class IngredientAdapterViewHolder extends RecyclerView.ViewHolder
    {
//        @BindView(R.id.ingredient_name) TextView mIngredientNameView;
//        @BindView(R.id.ingredient_measure) TextView mIngredientMeasureView;
//        @BindView(R.id.ingredient_unit) TextView mIngredientUnitView;

        public TextView mIngredientNameView;
        public TextView mIngredientMeasureView;
        public TextView mIngredientUnitView;

        public IngredientAdapterViewHolder(View view) {
            super(view);

            // not sure why this isn't working in here...
//            ButterKnife.bind(view);

            mIngredientNameView = view.findViewById(R.id.ingredient_name);
            mIngredientMeasureView = view.findViewById(R.id.ingredient_measure);
            mIngredientUnitView = view.findViewById(R.id.ingredient_unit);
        }
    }
}
