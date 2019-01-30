package app.com.example.android.bakingtime.RecipeUtils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import app.com.example.android.bakingtime.R;

public class RawJsonReader {

    //
    // deprecated method
    //
    public static List<Recipe> getRecipes(Context context){
        String jsonString;

        try {
            InputStream is = context.getResources().openRawResource(R.raw.raw_recipe);
            Writer writer = new StringWriter();
            char[] buffer = new char[10240];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                jsonString= writer.toString();

                return parseRecipesFromString(jsonString);
            }
            catch (Exception ex) {
            }
            finally {
                is.close();
            }
        }
        catch(Exception ex){
            int i = 0;
        }

        return null;
    }

    public static List<Recipe> parseRecipesFromString(String json){
        Type recipeListType = new TypeToken<Collection<Recipe>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, recipeListType);
    }

    public static String createJSONStringFromRecipes(List<Recipe> recipes){
        Gson gson = new Gson();
        return gson.toJson(recipes);
    }

    public static List<Ingredient> parseIngredientsFromString(String json){
        Type ingredientsListType = new TypeToken<Collection<Ingredient>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, ingredientsListType);
    }

    public static String createJSONStringFromIngredients(List<Ingredient> ingredients){
        Gson gson = new Gson();
        return gson.toJson(ingredients);
    }

    public static List<Step> parseStepsFromString(String json){
        Type stepsListType = new TypeToken<Collection<Step>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, stepsListType);
    }

    public static String createJSONStringFromSteps(List<Step> steps){
        Gson gson = new Gson();
        return gson.toJson(steps);
    }

    public static Step parseIndividualStepFromString(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, Step.class);
    }

    public static String createJSONStringFromIndividualStep(Step step){
        Gson gson = new Gson();
        return gson.toJson(step);
    }
}
