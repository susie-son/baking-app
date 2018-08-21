package me.susieson.bakingapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import me.susieson.bakingapp.models.Recipe;

@Database(entities = {Recipe.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context,
                        AppDatabase.class,
                        "recipes")
                        .build();
            }
        }
        return sInstance;
    }

    public abstract RecipeDao getRecipeDao();

}
