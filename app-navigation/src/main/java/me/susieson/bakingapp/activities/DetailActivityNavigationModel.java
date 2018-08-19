package me.susieson.bakingapp.activities;

import dart.BindExtra;
import dart.DartModel;
import me.susieson.bakingapp.models.Recipe;

@DartModel
public class DetailActivityNavigationModel {

    @BindExtra
    public Recipe selectedRecipe;

}
