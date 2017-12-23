package youtubeapidemo.examples.com.bakingapp;

import android.content.Context;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;
import static youtubeapidemo.examples.com.bakingapp.R.id.playerView;

public class DescriptionActivityFragment extends Fragment {
    public static final String VIDEO_URL = "video_url";
    private static final String STEP_NO = "step_no";
    private SimpleExoPlayerView mPlayerView;
    private Button mPreviousButton, mNextButton;
    private ArrayList<Description> mDescription;
    private int mCurrentPosition = 0;
    private TextView mStep, mDescriptionPosition;
    private int pos = 0;
    private View rootView;
    private TextView mNoVideoText;

    public DescriptionActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_description_activity, container, false);
        mPreviousButton = rootView.findViewById(R.id.previous_button);
        mNextButton = rootView.findViewById(R.id.next_button);
        mStep = rootView.findViewById(R.id.step_description);
        mDescriptionPosition = rootView.findViewById(R.id.description_position);
        mPlayerView = rootView.findViewById(playerView);

        Configuration configuration = getActivity().getResources().getConfiguration();
        int smallestScreenWidthDp = configuration.smallestScreenWidthDp;

        if(savedInstanceState!=null){
            mCurrentPosition=savedInstanceState.getInt(STEP_NO);
        }
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(IngredientActivityFragment.DESCRIPTION_ARRAY_LIST)) {
            mDescription = intent.getParcelableArrayListExtra(IngredientActivityFragment
                    .DESCRIPTION_ARRAY_LIST);

            mNoVideoText= rootView.findViewById(R.id.no_video_text);
            display();
            initializePlayer();
            Log.e(TAG,"1");
        } else if (configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            mNextButton.setVisibility(View.INVISIBLE);
            mPlayerView.setVisibility(View.INVISIBLE);
            Bundle bundle = getArguments();
            pos = bundle.getInt(IngredientActivity.ARG);
            mDescription = new ArrayList<>();
            makeJsonArrayRequest();

            Log.e(TAG,"2");
        }
        if ((configuration.orientation ==
                Configuration.ORIENTATION_PORTRAIT || configuration.orientation==
                Configuration.ORIENTATION_LANDSCAPE )&& smallestScreenWidthDp < 600 ) {
            display();
            initializePlayer();
        }
        if(configuration.orientation==Configuration.ORIENTATION_LANDSCAPE ){
            mNoVideoText= rootView.findViewById(R.id.no_video_text);

            Log.e(TAG,"3");
        }
        Log.e(TAG,"oncreate view");
        return rootView;
    }


    private void display() {
        mStep.setText(mDescription.get(mCurrentPosition).getDescription());
        SetUpPreviousButton();
        SetUpNextButton();

        Log.e(TAG,"diplay view");
    }

    private void SetUpPreviousButton(){

        Log.e(TAG,"setup previous");
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPosition--;
                if (mCurrentPosition == 0) {
                    mPreviousButton.setVisibility(View.INVISIBLE);
                    mDescriptionPosition.setVisibility(View.INVISIBLE);
                }
                if(mNoVideoText!=null)
                    mNoVideoText.setVisibility(View.INVISIBLE);
                mStep.setText(mDescription.get(mCurrentPosition).getDescription());
                mNextButton.setVisibility(View.VISIBLE);
                mDescriptionPosition.setText("" + (mCurrentPosition) + "/" + (mDescription.size() - 1));
                mStep.setText(mDescription.get(mCurrentPosition).getDescription());

                ExoPlayerHandler.getInstance().goToBackground();
                ExoPlayerHandler.getInstance().releaseVideoPlayer();
                initializePlayer();
            }
        });
    }


    private void SetUpNextButton(){

        Log.e(TAG,"setup next");
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPosition++;
                if (mCurrentPosition == mDescription.size() - 1) {
                    mNextButton.setVisibility(View.INVISIBLE);
                }
                if(mNoVideoText!=null)
                    mNoVideoText.setVisibility(View.INVISIBLE);
                mStep.setText(mDescription.get(mCurrentPosition).getDescription());
                mPreviousButton.setVisibility(View.VISIBLE);
                mDescriptionPosition.setText("" + (mCurrentPosition) + "/" + (mDescription.size() - 1));
                mDescriptionPosition.setVisibility(View.VISIBLE);
                ExoPlayerHandler.getInstance().goToBackground();
                ExoPlayerHandler.getInstance().releaseVideoPlayer();
                initializePlayer();
            }
        });
    }
    private void makeJsonArrayRequest() {

        Log.e(TAG,"makejsonarrayReq");
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

        Log.e(TAG,"initializePlayer");
        if(mPlayerView!=null)
        mPlayerView.setVisibility(View.VISIBLE);
        final String url = mDescription.get(mCurrentPosition).getVideoURL();
        if (url == null || url.isEmpty()) {
            if(mPlayerView!=null)
            mPlayerView.setVisibility(View.GONE);
            if(mNoVideoText!=null)
            mNoVideoText.setVisibility(View.VISIBLE);
            return;
        }
        if (mPlayerView != null) {
            ExoPlayerHandler.getInstance()
                    .prepareExoPlayerForUri(rootView.getContext(),
                            Uri.parse(url), mPlayerView);
            ExoPlayerHandler.getInstance().goToForeground();
            rootView.findViewById(R.id.exo_fullscreen_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context context = rootView.getContext();
                            Intent intent = new Intent(context,
                                    FullScreenVideoActivity.class);
                            intent.putExtra(VIDEO_URL, url);
                            context.startActivity(intent);
                        }
                    });
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.e(TAG,"onPause");
        ExoPlayerHandler.getInstance().goToBackground();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.e(TAG,"oncDestroyView");
        ExoPlayerHandler.getInstance().releaseVideoPlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_NO,mCurrentPosition);
    }
}
