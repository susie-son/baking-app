package me.susieson.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import dart.Dart;
import dart.DartModel;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.fragments.RecipeDetailFragment;
import me.susieson.bakingapp.fragments.RecipeDetailFragmentBuilder;
import me.susieson.bakingapp.models.Recipe;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    @DartModel
    DetailActivityNavigationModel detailActivityNavigationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Timber.d("Executing onCreate");

        Dart.bind(this);

        Recipe selectedRecipe = detailActivityNavigationModel.selectedRecipe;

        if (selectedRecipe != null) {
            String recipeName = selectedRecipe.getName();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(recipeName);
            }
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragmentBuilder(selectedRecipe)
                    .build();

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_recipe_detail_container, recipeDetailFragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
