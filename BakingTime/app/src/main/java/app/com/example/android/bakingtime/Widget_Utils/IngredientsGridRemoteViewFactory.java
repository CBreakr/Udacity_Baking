
package app.com.example.android.bakingtime.Widget_Utils;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import app.com.example.android.bakingtime.BakingTimeAppWidget;
import app.com.example.android.bakingtime.R;
import app.com.example.android.bakingtime.RecipeUtils.Ingredient;

public class IngredientsGridRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Ingredient> mIngredientList = null;

    private int mRecipeIndex = -1;

    public IngredientsGridRemoteViewFactory(Context applicationContext){
        Log.d("WIDGETCLICK", "ingredient display");

        mContext = applicationContext;

        Log.d("WIDGETCLICK", "ingredient display END CONSTRUCTOR");
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        checkIngredients();

        if(mIngredientList != null){
            return mIngredientList.size();
        }
        return 0;
    }

    private void checkIngredients(){
        int index = BakingTimeAppWidget.getCurrentRecipeIndex();

        Log.d("WIDGETCLICK", "ingredients factory COMPARE indexes: " + String.valueOf(index) + ", " + String.valueOf(mRecipeIndex));

        if(index != mRecipeIndex){
            mRecipeIndex = index;

            Log.d("WIDGETCLICK", "ingredients factory: " + String.valueOf(index));

            mIngredientList = BakingTimeAppWidget.getRecipeList(mContext).get(index).getIngredients();
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    // similar to onBindViewHolder in an adapter
    @Override
    public RemoteViews getViewAt(int index) {
        if (getCount() == 0) return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_item);

        // set view elements
        Ingredient ingredient = mIngredientList.get(index);
        views.setTextViewText(R.id.widget_ingredient_name, ingredient.getIngredient());
        views.setTextViewText(R.id.widget_ingredient_amount
                , ingredient.getQuantity() + " " + ingredient.getUnitOfMeasure().toLowerCase());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
