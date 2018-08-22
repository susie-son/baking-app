package me.susieson.bakingapp.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import me.susieson.bakingapp.models.Ingredient;

public class IngredientsTypeConverter {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static List<Ingredient> stringToIngredientList(String ingredientString) {
        if (ingredientString == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Ingredient>>() {
        }.getType();

        return gson.fromJson(ingredientString, listType);
    }

    @TypeConverter
    public static String ingredientListToString(List<Ingredient> ingredientList) {
        return gson.toJson(ingredientList);
    }

}
