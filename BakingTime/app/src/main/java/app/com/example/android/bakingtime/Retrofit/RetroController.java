package app.com.example.android.bakingtime.Retrofit;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.com.example.android.bakingtime.MainActivity;
import app.com.example.android.bakingtime.RecipeUtils.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroController implements Callback<List<Recipe>> {

    static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    private MainActivity parentActivity;

    public RetroController(MainActivity parent){
        parentActivity = parent;
    }

    public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetroAPI retroAPI = retrofit.create(RetroAPI.class);

        parentActivity.setIsNotIdle();
        Call<List<Recipe>> call = retroAPI.loadRecipes();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        if(response.isSuccessful()) {
            List<Recipe> recipeList = response.body();
            parentActivity.completeInitialSetup(recipeList);
        } else {
            System.out.println(response.errorBody());
        }
        parentActivity.setIsIdle();
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t)
    {
        parentActivity.setIsIdle();
        t.printStackTrace();
    }
}