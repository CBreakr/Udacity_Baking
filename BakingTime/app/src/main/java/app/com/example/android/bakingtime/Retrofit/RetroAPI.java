package app.com.example.android.bakingtime.Retrofit;

import java.util.List;

import app.com.example.android.bakingtime.RecipeUtils.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetroAPI {
    @GET("baking.json")
    Call<List<Recipe>> loadRecipes();
}