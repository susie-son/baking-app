package me.susieson.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.BindBool;
import butterknife.ButterKnife;
import dart.Dart;
import dart.DartModel;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.fragments.RecipeDetailFragment;
import me.susieson.bakingapp.fragments.RecipeDetailFragmentBuilder;
import me.susieson.bakingapp.fragments.RecipeStepFragment;
import me.susieson.bakingapp.fragments.RecipeStepFragmentBuilder;
import me.susieson.bakingapp.interfaces.OnItemClickListener;
import me.susieson.bakingapp.models.Recipe;
import me.susieson.bakingapp.models.Step;
import me.susieson.bakingapp_navigation.HensonNavigator;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements OnItemClickListener {

    @DartModel
    DetailActivityNavigationModel detailActivityNavigationModel;

    @BindBool(R.bool.is_two_pane)
    boolean isTwoPane;

    private FragmentManager mFragmentManager;
    private Recipe mSelectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("Executing onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        Dart.bind(this);

        mFragmentManager = getSupportFragmentManager();

        mSelectedRecipe = detailActivityNavigationModel.selectedRecipe;

        if (mSelectedRecipe != null) {
            String recipeName = mSelectedRecipe.getName();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(recipeName);
            }
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragmentBuilder(mSelectedRecipe)
                    .build();

            if (savedInstanceState == null) {
                mFragmentManager.beginTransaction()
                        .add(R.id.fragment_recipe_detail_container, recipeDetailFragment)
                        .commit();

                if (isTwoPane) {
                    Step step = mSelectedRecipe.getSteps().get(0);
                    RecipeStepFragment recipeStepFragment = new RecipeStepFragmentBuilder(step).build();

                    mFragmentManager.beginTransaction()
                            .add(R.id.fragment_recipe_step_container, recipeStepFragment)
                            .commit();
                }
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

    @Override
    public void onItemClick(int position) {
        if (isTwoPane) {
            Step step = mSelectedRecipe.getSteps().get(position);

            RecipeStepFragment recipeStepFragment = new RecipeStepFragmentBuilder(step).build();

            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_recipe_step_container, recipeStepFragment)
                    .commit();
        } else {
            Intent intent = HensonNavigator.gotoStepActivity(this)
                    .selectedRecipe(mSelectedRecipe)
                    .selectedStepNumber(position)
                    .build();
            startActivity(intent);
        }
    }
}
