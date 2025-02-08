package model;

import java.util.List;

public class OrderData {
    List<String> ingredients;

    public OrderData(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public OrderData() {
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
