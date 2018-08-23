package me.susieson.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import me.susieson.bakingapp.R;
import me.susieson.bakingapp.database.AppDatabase;
import me.susieson.bakingapp.models.Recipe;
import me.susieson.bakingapp.services.ListViewsService;
import me.susieson.bakingapp.utils.AppExecutors;
import me.susieson.bakingapp.utils.PreferencesUtils;
import me.susieson.bakingapp_navigation.HensonNavigator;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        final Intent intent = new Intent(context, ListViewsService.class);
        intent.setData(Uri.fromParts("content", String.valueOf(appWidgetId), null));
        views.setRemoteAdapter(R.id.recipe_widget_list_view, intent);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int recipeId = PreferencesUtils.getRecipeId(context, appWidgetId);
                if (recipeId != -1) {
                    Recipe selectedRecipe = AppDatabase.getInstance(context).getRecipeDao().getAllRecipes().get(recipeId);

                    Intent appIntent = HensonNavigator.gotoDetailActivity(context)
                            .selectedRecipe(selectedRecipe).build();

                    PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);

                    views.setOnClickPendingIntent(R.id.recipe_widget_label, appPendingIntent);

                    // Instruct the widget manager to update the widget
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.recipe_widget_list_view);
                }
            }
        });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

