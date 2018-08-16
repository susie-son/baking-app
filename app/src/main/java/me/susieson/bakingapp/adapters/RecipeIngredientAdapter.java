package me.susieson.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.constants.Measurement;
import me.susieson.bakingapp.models.Ingredient;
import timber.log.Timber;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder> {

    private List<Ingredient> mIngredientList;

    public RecipeIngredientAdapter(List<Ingredient> ingredientList) {
        mIngredientList = ingredientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context mContext = viewGroup.getContext();
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_recipe_ingredient, viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }

    public void updateData(List<Ingredient> ingredientList) {
        mIngredientList = ingredientList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_recipe_ingredient_text_view)
        TextView mIngredientTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            Ingredient ingredient = mIngredientList.get(position);

            String name = ingredient.getIngredient();
            double quantity = ingredient.getQuantity();
            String measure = ingredient.getMeasure();

            String ingredientDescription = format(name, quantity, measure);

            if (name != null && !TextUtils.isEmpty(name)) {
                mIngredientTextView.setText(ingredientDescription);
            }
        }

        private String format(String name, double quantity, String measure) {
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
}
