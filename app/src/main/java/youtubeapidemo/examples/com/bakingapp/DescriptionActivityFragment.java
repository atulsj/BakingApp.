package youtubeapidemo.examples.com.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static youtubeapidemo.examples.com.bakingapp.R.id.playerView;


public class DescriptionActivityFragment extends Fragment /*implements ExoPlayer.EventListener */ {
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mSimpleExoPlayer;
    private Button mPreviousButton, mNextButton;
    private ArrayList<Description> mDescription;
    private boolean playWhenReady = true;
    private int mCurrentPosition = 0;
    private TextView mStep, mDescriptionPosition;
    private int pos = 0;


    public DescriptionActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_description_activity, container, false);
        mPreviousButton = view.findViewById(R.id.previous_button);
        mNextButton = view.findViewById(R.id.next_button);
        mStep = view.findViewById(R.id.step_description);
        mDescriptionPosition = view.findViewById(R.id.description_position);
        mPlayerView = view.findViewById(playerView);


        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(IngredientActivityFragment.DESCRIPTION_ARRAY_LIST)) {
            mDescription = intent.getParcelableArrayListExtra(IngredientActivityFragment
                    .DESCRIPTION_ARRAY_LIST);
        } else if (getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            mNextButton.setVisibility(View.INVISIBLE);
            mPlayerView.setVisibility(View.INVISIBLE);
            Bundle bundle = getArguments();

            pos = bundle.getInt(IngredientActivity.ARG);
            Log.e("Yooooooooooooo", +pos + "");
            mDescription = new ArrayList<>();
            makeJsonArrayRequest();
        }

        if (getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            display();
            initializePlayer();
        }
        return view;
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
                initializePlayer();

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
                initializePlayer();

            }
        });

    }

    private void makeJsonArrayRequest() {
        String mUrlBaking = "https://go.udacity.com/android-baking-app-json";
        JsonArrayRequest req = new JsonArrayRequest(mUrlBaking,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject recipe = (JSONObject) response.get(pos);
                            JSONArray steps = recipe.getJSONArray("steps");
                            for (int i = 0; i < steps.length(); i++) {
                                JSONObject step = (JSONObject) steps.get(i);
                                int id = step.getInt("id");
                                String shortDescription = step.getString("shortDescription");
                                String describe = step.getString("description");
                                String videoURL = step.getString("videoURL");
                                mDescription.add(new Description(id, shortDescription,
                                        describe, videoURL));
                            }
                            mNextButton.setVisibility(View.VISIBLE);
                            mPlayerView.setVisibility(View.VISIBLE);
                            display();
                            initializePlayer();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(req);

    }

    private void initializePlayer() {

        mPlayerView.setVisibility(View.VISIBLE);
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());
        mPlayerView.setPlayer(mSimpleExoPlayer);
        String url = mDescription.get(mCurrentPosition).getVideoURL();
        if (url == null || url.isEmpty()) {

            mPlayerView.setVisibility(View.GONE);
            return;
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
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }
    }
/*

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
*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }


    public static void updateArrayList(Intent intent) {
     /*   Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(IngredientActivityFragment.DESCRIPTION_ARRAY_LIST)) {
            mDescription = intent.getParcelableArrayListExtra(IngredientActivityFragment
                    .DESCRIPTION_ARRAY_LIST);
        }

            display();
            initializePlayer();*/

    }
}
