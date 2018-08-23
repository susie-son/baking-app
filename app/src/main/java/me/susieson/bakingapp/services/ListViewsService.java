package me.susieson.bakingapp.services;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.susieson.bakingapp.R;
import me.susieson.bakingapp.database.AppDatabase;
import me.susieson.bakingapp.models.Ingredient;
import me.susieson.bakingapp.models.Recipe;
import me.susieson.bakingapp.utils.PreferencesUtils;
import me.susieson.bakingapp.utils.StringUtils;

public class ListViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewsFactory(getApplicationContext(), intent);
    }

}

class ListViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private final AppDatabase mAppDatabase;
    private List<Ingredient> mIngredientList = new ArrayList<>();
    private int mAppWidgetId;

    ListViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        mAppDatabase = AppDatabase.getInstance(mContext);
        mAppWidgetId = Integer.valueOf(Objects.requireNonNull(intent.getData()).getSchemeSpecificPart());
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        int recipeId = PreferencesUtils.getRecipeId(mContext, mAppWidgetId);
        if (recipeId != -1) {
            Recipe selectedRecipe = mAppDatabase.getRecipeDao().getAllRecipes().get(recipeId);

            mIngredientList = selectedRecipe.getIngredients();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_recipe_widget);

        Ingredient ingredient = mIngredientList.get(position);
        String formattedIngredient = StringUtils.formatIngredientInfo(ingredient.getIngredient(),
                ingredient.getQuantity(),
                ingredient.getMeasure());

        views.setTextViewText(R.id.item_recipe_widget_name, formattedIngredient);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}