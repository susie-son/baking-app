package me.susieson.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import me.susieson.bakingapp.models.Step;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.ViewHolder> {

    private final OnItemClickListener mOnItemClickListener;
    private List<Step> mStepList;
    private Context mContext;

    public RecipeStepAdapter(List<Step> stepList, OnItemClickListener onItemClickListener) {
        mStepList = stepList;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_recipe_step, viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mStepList.size();
    }

    public void updateData(List<Step> stepList) {
        mStepList = stepList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_recipe_step_number_text_view)
        TextView mStepNumberTextView;

        @BindView(R.id.item_recipe_step_image_view)
        ImageView mThumbnailImageView;

        @BindView(R.id.item_recipe_step_short_description_text_view)
        TextView mShortDescriptionTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final int position) {
            Step step = mStepList.get(position);

            String stepNumber = String.valueOf(position);
            String thumbnailUrl = step.getThumbnailURL();
            String shortDescription = step.getShortDescription();

            if (!TextUtils.isEmpty(stepNumber)) {
                mStepNumberTextView.setText(stepNumber);
            }

            if (thumbnailUrl != null && !TextUtils.isEmpty(thumbnailUrl)) {
                Picasso.with(mContext).load(thumbnailUrl).error(R.drawable.ic_chef_hat).into(mThumbnailImageView);
            } else {
                mThumbnailImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_chef_hat));
            }

            if (shortDescription != null && !TextUtils.isEmpty(shortDescription)) {
                mShortDescriptionTextView.setText(shortDescription);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(position);
                }
            });
        }

    }
}
