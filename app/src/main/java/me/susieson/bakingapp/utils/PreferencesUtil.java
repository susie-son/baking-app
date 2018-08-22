package me.susieson.bakingapp.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class PreferencesUtil {

    private static final String PREF_KEY = "appwidget_recipe_id";

    public static void setRecipeId(Context context, int recipeId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_KEY, recipeId)
                .apply();
    }

    public static int getRecipeId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_KEY, -1);
    }

}
