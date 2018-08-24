package me.susieson.bakingapp.fragments;

import android.app.Activity;
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
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.evernote.android.state.bundlers.BundlerListParcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.adapters.RecipeMainAdapter;
import me.susieson.bakingapp.database.AppDatabase;
import me.susieson.bakingapp.database.RecipeDao;
import me.susieson.bakingapp.interfaces.OnItemClickListener;
import me.susieson.bakingapp.models.Recipe;
import me.susieson.bakingapp.services.RecipeService;
import me.susieson.bakingapp.utils.AppExecutors;
import me.susieson.bakingapp.utils.NetworkUtils;
import me.susieson.bakingapp_navigation.HensonNavigator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RecipeMainFragment extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    @BindView(R.id.fragment_recipe_main)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recipe_main_recycler_view)
    RecyclerView mRecyclerView;

    @BindInt(R.integer.recipe_main_grid_span_count)
    int RECYCLER_VIEW_SPAN_COUNT;

    @State(BundlerListParcelable.class)
    List<Recipe> mRecipeList = new ArrayList<>();

    private boolean forceRefresh = false;
    private RecipeMainAdapter mRecipeAdapter;
    private Context mContext;
    private RecipeDao mRecipeDao;
    private Executor mDiskIO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("Executing onCreate");
        super.onCreate(savedInstanceState);
        mContext = getContext();
        StateSaver.restoreInstanceState(this, savedInstanceState);
        setHasOptionsMenu(true);
        mRecipeDao = AppDatabase.getInstance(mContext).getRecipeDao();
        mDiskIO = AppExecutors.getInstance().diskIO();
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
            mRecipeAdapter.updateData(mRecipeList);
        } else {
            getRecipeList();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Timber.d("Executing onSaveInstanceState");
        StateSaver.saveInstanceState(this, outState);
    }

    @Override
    public void onItemClick(int position) {
        Timber.d("Item %d clicked, opening %s recipe", position, mRecipeList.get(position).getName());
        Recipe selectedRecipe = mRecipeList.get(position);
        Intent intent = HensonNavigator.gotoDetailActivity(mContext)
                .selectedRecipe(selectedRecipe)
                .build();
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        Timber.d("Executing onRefresh");
        getRecipeList();
        forceRefresh = true;
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
        Timber.d("Retrieving recipe list");
        mSwipeRefreshLayout.setRefreshing(true);
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            getRecipeListFromInternet();
        } else if (!forceRefresh) {
            getRecipeListFromDatabase();
        } else {
            showLoadingFailed();
            forceRefresh = false;
        }
    }

    private void getRecipeListFromInternet() {
        Timber.d("Retrieving recipes from internet");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RecipeService recipeService = retrofit.create(RecipeService.class);
        final Call<List<Recipe>> recipeListCall = recipeService.getRecipes();
        recipeListCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                Timber.d("Recipes retrieval from internet successful");
                mRecipeList = response.body();
                mRecipeAdapter.updateData(mRecipeList);
                mSwipeRefreshLayout.setRefreshing(false);
                mDiskIO.execute(new Runnable() {
                    @Override
                    public void run() {
                        mRecipeDao.insertRecipes(mRecipeList);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Timber.d("Recipes retrieval from internet failed");
                if (!forceRefresh) {
                    getRecipeListFromDatabase();
                } else {
                    showLoadingFailed();
                }
            }
        });
    }

    private void getRecipeListFromDatabase() {
        Timber.d("Retrieving recipes from database");
        mDiskIO.execute(new Runnable() {
            @Override
            public void run() {
                mRecipeList = mRecipeDao.getAllRecipes();

                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mRecipeList == null || mRecipeList.isEmpty()) {
                                showLoadingFailed();
                            } else {
                                Timber.d("Recipes retrieval from database successful");
                                mRecipeAdapter.updateData(mRecipeList);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    private void showLoadingFailed() {
        Timber.d("Recipes retrieval failed");
        Toast.makeText(mContext,
                R.string.recipe_main_loading_error,
                Toast.LENGTH_LONG).show();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView() {
        Timber.d("Setting up RecyclerView");
        LayoutManager mLayoutManager = new GridLayoutManager(mContext, RECYCLER_VIEW_SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecipeAdapter = new RecipeMainAdapter(mRecipeList, this);
        mRecyclerView.setAdapter(mRecipeAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        SlideInRightAnimator animator = new SlideInRightAnimator(new OvershootInterpolator(1f));
        animator.setAddDuration(750);

        mRecyclerView.setItemAnimator(animator);
    }

    private void setupRefreshLayout() {
        Timber.d("Setting up Refresh listener");
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

}
