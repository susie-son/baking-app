package me.susieson.bakingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.activities.DetailActivity;
import me.susieson.bakingapp.adapters.RecipeMainAdapter;
import me.susieson.bakingapp.interfaces.OnItemClickListener;
import me.susieson.bakingapp.models.Recipe;
import me.susieson.bakingapp.services.RecipeService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RecipeMainFragment extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String FRAGMENT_SELECTED_RECIPE = "fragment-selected-recipe";

    private static final String RECIPE_LIST_EXTRA = "recipe-list";
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    @BindView(R.id.fragment_recipe_main)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recipe_main_recycler_view)
    RecyclerView mRecyclerView;

    @BindInt(R.integer.recipe_main_grid_span_count)
    int RECYCLER_VIEW_SPAN_COUNT;

    private RecipeMainAdapter mRecipeAdapter;

    private Context mContext;

    private List<Recipe> mRecipeList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("Executing onCreate");
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        Timber.d("Executing onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_recipe_main, container, false);
        ButterKnife.bind(this, rootView);

        setupRecyclerView();
        setupRefreshLayout();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPE_LIST_EXTRA)) {
                mRecipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_EXTRA);
                mRecipeAdapter.updateData(mRecipeList);
            }
        } else {
            getRecipeList();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Timber.d("Saving instance state");
        outState.putParcelableArrayList(RECIPE_LIST_EXTRA, (ArrayList<Recipe>) mRecipeList);
    }

    @Override
    public void onItemClick(int position) {
        Timber.d("Item %d clicked, opening %s recipe", position, mRecipeList.get(position).getName());
        Intent intent = new Intent(mContext, DetailActivity.class);
        Recipe selectedRecipe = mRecipeList.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(FRAGMENT_SELECTED_RECIPE, selectedRecipe);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getRecipeList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_refresh:
                onRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRecipeList() {
        Timber.d("Retrieving recipe list from the internet");
        mSwipeRefreshLayout.setRefreshing(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RecipeService recipeService = retrofit.create(RecipeService.class);
        final Call<List<Recipe>> recipeListCall = recipeService.getRecipes();
        recipeListCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                Timber.d("Recipes retrieval successful");
                mRecipeList = response.body();
                mRecipeAdapter.updateData(mRecipeList);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Timber.d(t, "Recipes retrieval failed");
                Toast.makeText(mContext,
                        R.string.recipe_main_loading_error,
                        Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setupRecyclerView() {
        Timber.d("Setting up RecyclerView");
        LayoutManager mLayoutManager = new GridLayoutManager(mContext, RECYCLER_VIEW_SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecipeAdapter = new RecipeMainAdapter(mRecipeList, this);
        mRecyclerView.setAdapter(mRecipeAdapter);
    }

    private void setupRefreshLayout() {
        Timber.d("Setting up Refresh listener");
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

}
