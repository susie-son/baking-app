package me.susieson.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.activities.DetailActivity;
import me.susieson.bakingapp.adapters.RecipeIngredientAdapter;
import me.susieson.bakingapp.models.Ingredient;
import me.susieson.bakingapp.models.Recipe;
import timber.log.Timber;

public class RecipeDetailFragment extends Fragment {

    private static final String INGREDIENT_LIST_EXTRA = "ingredient-list";

    @BindView(R.id.recipe_detail_ingredients_recycler_view)
    RecyclerView mIngredientsRecyclerView;

    private RecipeIngredientAdapter mIngredientAdapter;

    private Context mContext;

    private List<Ingredient> mIngredientList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("Executing onCreate");
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("Executing onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        setupIngredientRecyclerView();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(INGREDIENT_LIST_EXTRA)) {
                mIngredientList = savedInstanceState.getParcelableArrayList(INGREDIENT_LIST_EXTRA);
            }
        } else {
            Bundle receiveBundle = getArguments();
            if (receiveBundle != null && receiveBundle.containsKey(DetailActivity.ACTIVITY_SELECTED_RECIPE)) {
                Recipe selectedRecipe = receiveBundle.getParcelable(DetailActivity.ACTIVITY_SELECTED_RECIPE);
                if (selectedRecipe != null) {
                    mIngredientList = selectedRecipe.getIngredients();
                }
            }
        }
        mIngredientAdapter.updateData(mIngredientList);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Timber.d("Saving instance state");
        outState.putParcelableArrayList(INGREDIENT_LIST_EXTRA, (ArrayList<Ingredient>) mIngredientList);
    }

    private void setupIngredientRecyclerView() {
        Timber.d("Setting up Ingredient RecyclerView");
        LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mIngredientsRecyclerView.setLayoutManager(mLayoutManager);

        mIngredientAdapter = new RecipeIngredientAdapter(mIngredientList);
        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);
    }
}
