package me.susieson.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.interfaces.OnItemClickListener;
import me.susieson.bakingapp.models.Recipe;

public class RecipeMainAdapter extends RecyclerView.Adapter<RecipeMainAdapter.ViewHolder> {

    private final OnItemClickListener mOnItemClickListener;
    private List<Recipe> mRecipeList;
    private Context mContext;

    public RecipeMainAdapter(List<Recipe> recipeList, final OnItemClickListener onItemClickListener) {
        mRecipeList = recipeList;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int viewType) {
        mContext = viewGroup.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_recipe_main, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        viewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    public void updateData(List<Recipe> recipeList) {
        mRecipeList = recipeList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_recipe_image_view)
        ImageView mRecipeImageView;

        @BindView(R.id.item_recipe_name_text_view)
        TextView mRecipeNameTextView;

        @BindView(R.id.item_recipe_serving_text_view)
        TextView mRecipeServingTextView;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final int position) {
            Recipe recipe = mRecipeList.get(position);

            String imageUrl = recipe.getImage();
            String name = recipe.getName();
            String servings = String.valueOf(recipe.getServings());

            if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
                Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_cutlery).into(mRecipeImageView);
            } else {
                mRecipeImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_cutlery));
            }

            if (name != null && !TextUtils.isEmpty(name)) {
                mRecipeNameTextView.setText(name);
            }

            mRecipeServingTextView.setText(servings);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(position);
                }
            });
        }
    }
}
