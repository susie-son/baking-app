package me.susieson.bakingapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.bakingapp.R;
import me.susieson.bakingapp.models.Step;
import timber.log.Timber;

@FragmentWithArgs
public class RecipeStepFragment extends Fragment {

    @Nullable
    @BindView(R.id.recipe_step_description_text_view)
    TextView mDescriptionTextView;

    @BindView(R.id.recipe_step_player_view)
    PlayerView mPlayerView;

    @BindView(R.id.recipe_step_player_view_placeholder_image_view)
    ImageView mPlaceholderImageView;

    @BindBool(R.bool.is_fullscreen)
    boolean isFullscreen;

    @Arg
    @State
    Step step;

    @State
    long playerPosition;

    private SimpleExoPlayer mExoPlayer;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("Executing onCreate");
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        FragmentArgs.inject(this);
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("Executing onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        ButterKnife.bind(this, rootView);

        String videoUrl = step.getVideoURL();

        if (!isFullscreen) {
            String description = step.getDescription();
            if (mDescriptionTextView != null) {
                mDescriptionTextView.setText(description);
            }
        }

        if (videoUrl != null && !TextUtils.isEmpty(videoUrl)) {
            initializePlayer(videoUrl);
        } else {
            mPlayerView.setVisibility(View.GONE);
            mPlaceholderImageView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Timber.d("Executing onSaveInstanceState");
        if (mExoPlayer != null) {
            playerPosition = mExoPlayer.getCurrentPosition();
        }
        StateSaver.saveInstanceState(this, outState);
    }

    @Override
    public void onStop() {
        Timber.d("Executing onStop");
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy() {
        Timber.d("Executing onDestroy");
        super.onDestroy();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void initializePlayer(String videoUrl) {
        if (mExoPlayer == null) {
            Timber.d("Initializing player");
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);
            mPlayerView.setShowBuffering(true);

            Uri videoUri = Uri.parse(videoUrl);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext, "BakingApp"));
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoUri);
            mExoPlayer.prepare(videoSource);
            mExoPlayer.seekTo(playerPosition);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

}
