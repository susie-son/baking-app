package me.susieson.bakingapp.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class PreferencesUtils {

    private static final String PREF_KEY = "appwidget_";

    public static void setRecipeId(Context context, int recipeId, int appWidgetId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_KEY + appWidgetId, recipeId)
                .apply();
    }

    public static int getRecipeId(Context context, int appWidgetId) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_KEY + appWidgetId, -1);
    }

}
