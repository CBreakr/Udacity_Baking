package app.com.example.android.bakingtime.Widget_Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import app.com.example.android.bakingtime.BakingTimeAppWidget;
import app.com.example.android.bakingtime.R;
import app.com.example.android.bakingtime.RecipeUtils.Recipe;

public class RecipesGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;

    public RecipesGridRemoteViewsFactory(Context applicationContext){
        mContext = applicationContext;
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
        return BakingTimeAppWidget.getRecipeListCount(mContext);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    // similar to onBindViewHolder in an adapter
    @Override
    public RemoteViews getViewAt(int index) {
        if (getCount() == 0) return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_recipe_item);

        // set view elements
        Recipe recipe = BakingTimeAppWidget.getRecipeList(mContext).get(index);
        views.setTextViewText(R.id.widget_recipe_name, recipe.getName());

        Bundle extras = new Bundle();
        extras.putInt(BakingTimeAppWidget.RECIPE_INDEX_EXTRA, index);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_recipe_item_layout, fillInIntent);

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
