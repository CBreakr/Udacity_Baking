package app.com.example.android.bakingtime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.List;

import app.com.example.android.bakingtime.RecipeUtils.RawJsonReader;
import app.com.example.android.bakingtime.RecipeUtils.Recipe;
import app.com.example.android.bakingtime.Widget_Utils.IngredientsGridWidgetService;
import app.com.example.android.bakingtime.Widget_Utils.RecipesGridWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class BakingTimeAppWidget extends AppWidgetProvider {

    private static final String RECIPE_CLICK_ACTION = "recipe_click";
    private static final String BACK_CLICK_ACTION = "back_click";

    public static final String RECIPE_INDEX_EXTRA = "recipe_index";

    private static List<Recipe> mRecipeList = null;

    private static boolean mIsIngredientState = false;

    private static Integer mRecipeIndex = null;

    public static List<Recipe> getRecipeList(Context context){
        if(mRecipeList == null){
            BakingTimeAppWidget.mRecipeList = RawJsonReader.getRecipes(context);
        }
        return mRecipeList;
    }

    public static int getRecipeListCount(Context context){
        if(getRecipeList(context) == null){
            return 0;
        }
        return getRecipeList(context).size();
    }

    public static int getCurrentRecipeIndex(){
        return mRecipeIndex.intValue();
    }

    private static void updateAllAppWidgets(
            Context context,
            AppWidgetManager appWidgetManager,
            int[] appWidgetIds)
    {
        for (int appWidgetId : appWidgetIds) {
            updateIndividualAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateIndividualAppWidget(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId)
    {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_app_widget);

        if(mIsIngredientState){
            Intent gridIntent = new Intent(context, IngredientsGridWidgetService.class);

            Log.d("WIDGETCLICK", "index selected: " + String.valueOf(mRecipeIndex));

            views.setRemoteAdapter(R.id.widget_grid_view, gridIntent);

            Intent backClickIntent = new Intent(context, BakingTimeAppWidget.class);
            backClickIntent.setAction(BACK_CLICK_ACTION);

            PendingIntent appPendingIntent = PendingIntent.getBroadcast(context, 0, backClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_recipe_return_button, appPendingIntent);

            // show the return button
            views.setViewVisibility(R.id.widget_recipe_return_button, View.VISIBLE);

            Log.d("WIDGETCLICK", "grid intent set: INGREDIENTS");

            // set the list empty view
            views.setEmptyView(R.id.widget_grid_view, R.id.ingredient_empty_view);
        }
        else {
            Intent gridIntent = new Intent(context, RecipesGridWidgetService.class);
            views.setRemoteAdapter(R.id.widget_grid_view, gridIntent);

            Intent recipeClickIntent = new Intent(context, BakingTimeAppWidget.class);
            recipeClickIntent.setAction(RECIPE_CLICK_ACTION);

            PendingIntent appPendingIntent = PendingIntent.getBroadcast(context, 0, recipeClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

            // hide the return button
            views.setViewVisibility(R.id.widget_recipe_return_button, View.GONE);

            Log.d("WIDGETCLICK", "grid intent set: RECIPES");

            // set the list empty view
            views.setEmptyView(R.id.widget_grid_view, R.id.recipe_empty_view);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        Log.d("WIDGETCLICK", "in the widget OnUpdate");
        updateAllAppWidgets(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d("WIDGETCLICK", "in the widget receive INSIDE: " + intent.getAction());

        if (intent.getAction().equals(RECIPE_CLICK_ACTION)) {
            mIsIngredientState = true;

            Bundle extras = intent.getExtras();
            mRecipeIndex = extras.getInt(RECIPE_INDEX_EXTRA);

            Log.d("WIDGETCLICK", "the recipe index clicked: " + String.valueOf(mRecipeIndex));
        }
        else if (intent.getAction().equals(BACK_CLICK_ACTION)) {
            mIsIngredientState = false;
            Log.d("WIDGETCLICK", "back action clicked");
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetId = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, BakingTimeAppWidget.class));

        updateAllAppWidgets(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

