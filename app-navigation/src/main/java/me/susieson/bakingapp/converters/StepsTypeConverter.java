package me.susieson.bakingapp.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import me.susieson.bakingapp.models.Step;

public class StepsTypeConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Step> stringToStepList(String stepString) {
        if (stepString == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Step>>() {
        }.getType();

        return gson.fromJson(stepString, listType);
    }

    @TypeConverter
    public static String stepListToString(List<Step> stepList) {
        return gson.toJson(stepList);
    }

}
