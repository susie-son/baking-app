package me.susieson.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import me.susieson.bakingapp.R;
import me.susieson.bakingapp.fragments.RecipeMainFragment;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.d("Executing onCreate");

        if (savedInstanceState == null) {
            Timber.plant(new Timber.DebugTree());
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeMainFragment mRecipeMainFragment = new RecipeMainFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_recipe_main_container, mRecipeMainFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_main, menu);
        return true;
    }
}
