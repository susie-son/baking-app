package me.susieson.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.adapters.RecipeMainAdapter;
import me.susieson.bakingapp.database.AppDatabase;
import me.susieson.bakingapp.database.RecipeDao;
import me.susieson.bakingapp.interfaces.OnItemClickListener;
import me.susieson.bakingapp.models.Recipe;
import me.susieson.bakingapp.utils.AppExecutors;
import me.susieson.bakingapp.utils.PreferencesUtil;
import timber.log.Timber;

/**
 * The configuration screen for the {@link RecipeWidgetProvider RecipeWidget} AppWidget.
 */
public class RecipeWidgetConfigureActivity extends AppCompatActivity implements OnItemClickListener {

    @BindView(R.id.recipe_widget_recycler_view)
    RecyclerView mRecyclerView;
    @BindInt(R.integer.recipe_main_grid_span_count)
    int RECYCLER_VIEW_SPAN_COUNT;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private List<Recipe> mRecipeList = new ArrayList<>();
    private RecipeMainAdapter mRecipeAdapter;
    private RecipeDao mRecipeDao;
    private Executor mDiskIO;

    public RecipeWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_widget_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.choose_recipe);
        }

        mRecipeDao = AppDatabase.getInstance(this).getRecipeDao();
        mDiskIO = AppExecutors.getInstance().diskIO();

        setupRecyclerView();
        getRecipeListFromDatabase();
    }

    @Override
    public void onItemClick(int position) {
        Timber.d("Item %d clicked, saving recipe pref", position);
        final Context context = RecipeWidgetConfigureActivity.this;

        PreferencesUtil.setRecipeId(context, position);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RecipeWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void getRecipeListFromDatabase() {
        Timber.d("Retrieving recipes from database");
        mDiskIO.execute(new Runnable() {
            @Override
            public void run() {
                mRecipeList = mRecipeDao.getAllRecipes();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mRecipeList == null || mRecipeList.isEmpty()) {
                            showLoadingFailed();
                        } else {
                            Timber.d("Recipes retrieval from database successful");
                            mRecipeAdapter.updateData(mRecipeList);
                        }
                    }
                });
            }
        });
    }

    private void showLoadingFailed() {
        Timber.d("Recipes retrieval failed");
        Toast.makeText(this,
                R.string.recipe_main_loading_error,
                Toast.LENGTH_LONG).show();
    }

    private void setupRecyclerView() {
        Timber.d("Setting up RecyclerView");
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, RECYCLER_VIEW_SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecipeAdapter = new RecipeMainAdapter(mRecipeList, this);
        mRecyclerView.setAdapter(mRecipeAdapter);
    }

}

