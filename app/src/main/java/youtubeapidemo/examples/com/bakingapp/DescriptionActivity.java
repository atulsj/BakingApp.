package youtubeapidemo.examples.com.bakingapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import static youtubeapidemo.examples.com.bakingapp.R.id.playerView;

public class DescriptionActivity extends AppCompatActivity implements ExoPlayer.EventListener {
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mSimpleExoPlayer;
    private Button mPreviousButton, mNextButton;
    private ArrayList<Description> mDescription;
    private boolean playWhenReady = true;
    private int mCurrentPosition = 0;
    private TextView mStep, mDescriptionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);


        mPreviousButton = (Button) findViewById(R.id.previous_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mStep = (TextView) findViewById(R.id.step_description);
        mDescriptionPosition = (TextView) findViewById(R.id.description_position);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(IngredientActivityFragment.DESCRIPTION_ARRAY_LIST)) {
            mDescription = intent.getParcelableArrayListExtra(IngredientActivityFragment.DESCRIPTION_ARRAY_LIST);
        }

        display();
        initializePlayer();
    }

    private void display() {
        mStep.setText(mDescription.get(mCurrentPosition).getDescription());

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPosition++;
                if (mCurrentPosition == mDescription.size() - 1) {
                    mNextButton.setVisibility(View.INVISIBLE);
                }

                mStep.setText(mDescription.get(mCurrentPosition).getDescription());
                mPreviousButton.setVisibility(View.VISIBLE);
                mDescriptionPosition.setText("" + (mCurrentPosition) + "/" + (mDescription.size() - 1));
                mDescriptionPosition.setVisibility(View.VISIBLE);
                releasePlayer();
                if ((Util.SDK_INT <= 23 || mSimpleExoPlayer == null)) {
                    initializePlayer();
                } else if (Util.SDK_INT > 23) {
                    initializePlayer();
                }

            }
        });
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPosition--;
                if (mCurrentPosition == 0) {
                    mPreviousButton.setVisibility(View.INVISIBLE);
                    mDescriptionPosition.setVisibility(View.INVISIBLE);
                }

                mStep.setText(mDescription.get(mCurrentPosition).getDescription());
                mNextButton.setVisibility(View.VISIBLE);
                mDescriptionPosition.setText("" + (mCurrentPosition) + "/" + (mDescription.size() - 1));
                mStep.setText(mDescription.get(mCurrentPosition).getDescription());
                releasePlayer();
                if ((Util.SDK_INT <= 23 || mSimpleExoPlayer == null)) {
                    initializePlayer();
                } else if (Util.SDK_INT > 23) {
                    initializePlayer();
                }
            }
        });

    }

    private void initializePlayer() {
        mPlayerView = (SimpleExoPlayerView) findViewById(playerView);
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());
        mPlayerView.setPlayer(mSimpleExoPlayer);
        String url = mDescription.get(mCurrentPosition).getVideoURL();
        if(url==null || url.isEmpty()) {
            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.no_video));
            url = "abc";
        }
            Uri uri = Uri.parse(url);
            MediaSource mediaSource = buildMediaSource(uri);
            mSimpleExoPlayer.prepare(mediaSource, true, false);

        mSimpleExoPlayer.setPlayWhenReady(playWhenReady);

    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void releasePlayer() {
        mSimpleExoPlayer.stop();
        mSimpleExoPlayer.release();
        mSimpleExoPlayer = null;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
