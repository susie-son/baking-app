package me.susieson.bakingapp.services;

import java.util.List;

import me.susieson.bakingapp.models.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeService {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();

}
