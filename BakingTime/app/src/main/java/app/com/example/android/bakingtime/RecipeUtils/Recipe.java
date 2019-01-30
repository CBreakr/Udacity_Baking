package app.com.example.android.bakingtime.RecipeUtils;

import java.util.List;

public class Recipe {
    private String id;
    private String name;
    private String servings;
    private String image;
    private List<Ingredient> ingredients;
    private List<Step> steps;


    public String getId() {
        return id;
    }

    public void setId(String mId) {
        this.id = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String mServings) {
        this.servings = mServings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String mImage) {
        this.image = mImage;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> mIngredients) {
        this.ingredients = mIngredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> mSteps) {
        this.steps = mSteps;
    }
}
