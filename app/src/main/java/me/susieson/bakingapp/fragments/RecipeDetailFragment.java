package me.susieson.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
import me.susieson.bakingapp.adapters.RecipeStepAdapter;
import me.susieson.bakingapp.interfaces.OnItemClickListener;
import me.susieson.bakingapp.models.Ingredient;
import me.susieson.bakingapp.models.Recipe;
import me.susieson.bakingapp.models.Step;
import timber.log.Timber;

public class RecipeDetailFragment extends Fragment implements OnItemClickListener {

    private static final String INGREDIENT_LIST_EXTRA = "ingredient-list";
    private static final String STEP_LIST_EXTRA = "step-list";

    @BindView(R.id.recipe_detail_ingredients_recycler_view)
    RecyclerView mIngredientsRecyclerView;

    @BindView(R.id.recipe_detail_steps_recycler_view)
    RecyclerView mStepsRecyclerView;

    private RecipeIngredientAdapter mIngredientAdapter;
    private RecipeStepAdapter mStepAdapter;
    private Context mContext;
    private List<Ingredient> mIngredientList = new ArrayList<>();
    private List<Step> mStepList = new ArrayList<>();

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
        setupStepRecyclerView();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(INGREDIENT_LIST_EXTRA)) {
                mIngredientList = savedInstanceState.getParcelableArrayList(INGREDIENT_LIST_EXTRA);
            }
            if (savedInstanceState.containsKey(STEP_LIST_EXTRA)) {
                mStepList = savedInstanceState.getParcelableArrayList(STEP_LIST_EXTRA);
            }
        } else {
            Bundle receiveBundle = getArguments();
            if (receiveBundle != null && receiveBundle.containsKey(DetailActivity.ACTIVITY_SELECTED_RECIPE)) {
                Recipe selectedRecipe = receiveBundle.getParcelable(DetailActivity.ACTIVITY_SELECTED_RECIPE);
                if (selectedRecipe != null) {
                    mIngredientList = selectedRecipe.getIngredients();
                    mStepList = selectedRecipe.getSteps();
                }
            }
        }
        mIngredientAdapter.updateData(mIngredientList);
        mStepAdapter.updateData(mStepList);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Timber.d("Saving instance state");
        outState.putParcelableArrayList(INGREDIENT_LIST_EXTRA, (ArrayList<Ingredient>) mIngredientList);
        outState.putParcelableArrayList(STEP_LIST_EXTRA, (ArrayList<Step>) mStepList);
    }

    @Override
    public void onItemClick(int position) {
        Timber.i("Item %d clicked!", position);
    }

    private void setupIngredientRecyclerView() {
        Timber.d("Setting up Ingredient RecyclerView");
        LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mIngredientsRecyclerView.setLayoutManager(layoutManager);

        mIngredientAdapter = new RecipeIngredientAdapter(mIngredientList);
        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);
        mIngredientsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setupStepRecyclerView() {
        Timber.d("Setting up Step RecyclerView");
        LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mStepsRecyclerView.setLayoutManager(layoutManager);

        mStepAdapter = new RecipeStepAdapter(mStepList, this);
        mStepsRecyclerView.setAdapter(mStepAdapter);
        mStepsRecyclerView.setNestedScrollingEnabled(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        mStepsRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
