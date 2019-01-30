package app.com.example.android.bakingtime.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import app.com.example.android.bakingtime.R;

import java.util.List;

import app.com.example.android.bakingtime.RecipeUtils.Recipe;

//import butterknife.BindView;
//import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private List<Recipe> mRecipeData;
    private int mCurrentRecipeIndex;

    private static final int SELECTED = 100;
    private static final int UNSELECTED = 200;

    private final RecipeAdapterClickHandler mClickHandler;

    public interface RecipeAdapterClickHandler{
        void onClick(int index);
    }

    public RecipeAdapter(RecipeAdapterClickHandler clickHandler, int currentRecipeIndex) {
        mClickHandler = clickHandler;
        mCurrentRecipeIndex = currentRecipeIndex;
    }

    public void setRecipeData(List<Recipe> recipes) {
        mRecipeData = recipes;
        notifyDataSetChanged();
    }

    /*
    OVERRIDE METHODS
     */

    @Override
    public int getItemViewType(int position) {
        if(position == mCurrentRecipeIndex){
            return SELECTED;
        }
        return UNSELECTED;
    }

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem;

//        viewType == SELECTED
        layoutIdForListItem = R.layout.recipe_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RecipeAdapterViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final RecipeAdapterViewHolder recipeAdapterViewHolder, int position) {
        Recipe currentRecipe = mRecipeData.get(position);

        String recipeImage = currentRecipe.getImage();
        if(recipeImage == null || recipeImage.equals("")){
            recipeAdapterViewHolder.mRecipeImage.setVisibility(View.GONE);
        }
        else {
            Picasso.with(recipeAdapterViewHolder.mContext)
                    .load(currentRecipe.getImage())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(recipeAdapterViewHolder.mRecipeImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            recipeAdapterViewHolder.mRecipeImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            recipeAdapterViewHolder.mRecipeImage.setVisibility(View.GONE);
                        }
                    });
        }

        recipeAdapterViewHolder.mRecipeName.setText(currentRecipe.getName());
        recipeAdapterViewHolder.mRecipeServings.setText(currentRecipe.getServings());
    }

    @Override
    public int getItemCount() {
        if (null == mRecipeData) return 0;
        return mRecipeData.size();
    }

    /*
    VIEWHOLDER
     */

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        // image, name, servings
        /*
        @BindView(R.id.recipe_image) ImageView mRecipeImage;
        @BindView(R.id.recipe_name) TextView mRecipeName;
        @BindView(R.id.servings_value) TextView mRecipeServings;
        */

        public ImageView mRecipeImage;
        public TextView mRecipeName;
        public TextView mRecipeServings;

        public Context mContext;

        public RecipeAdapterViewHolder(View view, Context context) {
            super(view);
            mContext = context;
            //ButterKnife.bind(view);
            mRecipeImage = view.findViewById(R.id.recipe_image);
            mRecipeName = view.findViewById(R.id.recipe_name);
            mRecipeServings = view.findViewById(R.id.servings_value);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position);
        }
    }
}
