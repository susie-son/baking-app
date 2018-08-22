package me.susieson.bakingapp.utils;

import me.susieson.bakingapp.constants.Measurement;
import timber.log.Timber;

public class StringUtils {

    public static String formatIngredientInfo(String name, double quantity, String measure) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(quantity);
        stringBuilder.append(" ");

        String measurement;
        boolean isUnit = false;
        boolean measurementExists = true;

        try {
            Measurement measurementType = Measurement.valueOf(measure);
            measurement = measurementType.getDescription();
            if (measurementType.isUnit()) {
                isUnit = true;
            }
        } catch (IllegalArgumentException e) {
            Timber.e(e, "No such measurement type %s in Measurement enum", measure);
            measurement = measure.toLowerCase();
            measurementExists = false;
        }
        stringBuilder.append(measurement);

        if (quantity > 1.0 && !isUnit && measurementExists) {
            stringBuilder.append("s");
        }

        if (!isUnit) {
            stringBuilder.append(" of ");
        }

        stringBuilder.append(name);

        return stringBuilder.toString();
    }

}
