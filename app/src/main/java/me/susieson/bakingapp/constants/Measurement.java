package me.susieson.bakingapp.constants;

public enum Measurement {
    CUP("cup"), TBLSP("tablespoon"), TSP("teaspoon"), K("kilogram"), G("gram"), OZ("ounce"), UNIT("");

    private final String mDescription;

    Measurement(String description) {
        mDescription = description;
    }

    public boolean isUnit() {
        return this == UNIT;
    }

    public String getDescription() {
        return mDescription;
    }

}
