package me.susieson.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.susieson.bakingapp.R;
import me.susieson.bakingapp.fragments.RecipeDetailFragment;
import me.susieson.bakingapp.fragments.RecipeMainFragment;
import me.susieson.bakingapp.models.Recipe;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    public static final String ACTIVITY_SELECTED_RECIPE = "activity-selected-recipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Timber.d("Executing onCreate");

        Bundle receiveBundle = getIntent().getExtras();
        if (receiveBundle != null && savedInstanceState == null && receiveBundle.containsKey(RecipeMainFragment.FRAGMENT_SELECTED_RECIPE)) {
            Recipe selectedRecipe = receiveBundle.getParcelable(RecipeMainFragment.FRAGMENT_SELECTED_RECIPE);

            if (selectedRecipe != null) {
                String recipeName = selectedRecipe.getName();
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(recipeName);
                }
            }

            Bundle sendBundle = new Bundle();
            sendBundle.putParcelable(ACTIVITY_SELECTED_RECIPE, selectedRecipe);

            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            recipeDetailFragment.setArguments(sendBundle);
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_recipe_detail_container, recipeDetailFragment)
                    .commit();
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
