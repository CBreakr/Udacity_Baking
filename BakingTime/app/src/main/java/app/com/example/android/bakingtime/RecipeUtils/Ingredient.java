package app.com.example.android.bakingtime.RecipeUtils;

public class Ingredient {
    private String ingredient;
    private String quantity;
    private String measure;

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String mName) {
        this.ingredient = mName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String mQuantity) {
        this.quantity = mQuantity;
    }

    public String getUnitOfMeasure() {
        return measure;
    }

    public void setUnitOfMeasure(String mUnitOfMeasure) {
        this.measure = mUnitOfMeasure;
    }
}
