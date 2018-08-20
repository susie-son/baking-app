package me.susieson.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindBool;
import butterknife.ButterKnife;
import dart.Dart;
import dart.DartModel;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.fragments.RecipeStepFragment;
import me.susieson.bakingapp.fragments.RecipeStepFragmentBuilder;
import me.susieson.bakingapp.models.Recipe;
import me.susieson.bakingapp.models.Step;

public class StepActivity extends AppCompatActivity {

    @DartModel
    StepActivityNavigationModel stepActivityNavigationModel;

    @BindBool(R.bool.is_fullscreen)
    boolean isFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);
        Dart.bind(this);

        Recipe selectedRecipe = stepActivityNavigationModel.selectedRecipe;
        int selectedStepNumber = stepActivityNavigationModel.selectedStepNumber;

        String recipeName = selectedRecipe.getName();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(recipeName);
            if (isFullscreen) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                actionBar.hide();
            }
        }

        Step selectedStep = selectedRecipe.getSteps().get(selectedStepNumber);

        RecipeStepFragment recipeStepFragment = new RecipeStepFragmentBuilder(selectedStep).build();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_recipe_step_container, recipeStepFragment)
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
