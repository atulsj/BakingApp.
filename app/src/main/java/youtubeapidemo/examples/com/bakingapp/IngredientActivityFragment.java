package youtubeapidemo.examples.com.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class IngredientActivityFragment extends Fragment {
    // @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    // @BindView(R.id.cook_custom_button)
    Button cookingButton;
    // @BindView(R.id.card_view_ingredients)
    CardView cardViewIngredients;

    public static final String TAG = IngredientActivityFragment.class.getSimpleName();
    private IngredientAdapter ingredientAdapter;
    public static int pos;
    private ArrayList<String> arrayList;
    public static ArrayList<Description> descriptionArrayList;
    public static final String WIDGIT_POSITION = "WIDGIT POSITION";
    public static final String DESCRIPTION_ARRAY_LIST = "list";
    private TextView ingredientHead;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ingredient_activity, container, false);
        //ButterKnife.bind(this, root);
        recyclerView = root.findViewById(R.id.recycler_view);
        if (getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            cookingButton = root.findViewById(R.id.cook_custom_button);
            cardViewIngredients = root.findViewById(R.id.card_view_ingredients);
        }
        ingredientHead = root.findViewById(R.id.ingredient_head);

        arrayList = new ArrayList<>();
        descriptionArrayList = new ArrayList<>();
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if ((intent.getAction()).equals(WIDGIT_POSITION)) {
                pos = intent.getIntExtra("WIDGIT_POSITION_CLICKED", 0);
            } else if (intent.hasExtra(MainActivity.BUNDLE)) {
                Bundle bundle = intent.getBundleExtra(MainActivity.BUNDLE);
                pos = bundle.getInt(MainActivity.LIST_KEY);
            }
        }
        // if (NetworkReceiver.isNetworkOnline(getActivity())) {
        makeJsonArrayRequest();
        // }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientAdapter = new IngredientAdapter(getActivity(), arrayList);
        recyclerView.setAdapter(ingredientAdapter);
        if (getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
            recyclerView.addOnItemTouchListener(disabler);
        }
        return root;
    }


    private void makeJsonArrayRequest() {
        String mUrlBaking = "https://go.udacity.com/android-baking-app-json";
        JsonArrayRequest req = new JsonArrayRequest(mUrlBaking,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String ingredientsRequirent ;
                        try {
                            JSONObject recipe = (JSONObject) response.get(pos);
                            JSONArray ingredients = recipe.getJSONArray("ingredients");
                            for (int j = 0; j < ingredients.length(); j++) {
                                JSONObject ingr = (JSONObject) ingredients.get(j);
                                ingredientsRequirent = ingr.getString("ingredient") + " - " +
                                        ingr.getDouble("quantity") + " " + ingr.getString("measure");
                                arrayList.add(ingredientsRequirent);
                            }

                            /*Cooking button,card View  is not available in Landscape layout*/

                                JSONArray steps = recipe.getJSONArray("steps");
                                for (int i = 0; i < steps.length(); i++) {
                                    JSONObject step = (JSONObject) steps.get(i);
                                    int id = step.getInt("id");
                                    String shortDescription = step.getString("shortDescription");
                                    String describe = step.getString("description");
                                    String videoURL = step.getString("videoURL");
                                    descriptionArrayList.add(new Description(id, shortDescription,
                                            describe, videoURL));
                                }
                            if (getActivity().getResources().getConfiguration().orientation ==
                                    Configuration.ORIENTATION_PORTRAIT) {

                                cookingButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), DescriptionActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelableArrayList(DESCRIPTION_ARRAY_LIST,
                                                descriptionArrayList);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });

                                cookingButton.setVisibility(View.VISIBLE);
                                cardViewIngredients.setVisibility(View.VISIBLE);

                            }
                            ingredientHead.setVisibility(View.VISIBLE);
                            ingredientAdapter.changeData(arrayList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "Error: " + e.getMessage());
                        } finally {
                            if (IngredientActivity.mProgressBar != null)
                                IngredientActivity.mProgressBar.setVisibility(View.INVISIBLE);
                            if (MainActivity.mProgressBar != null)
                                MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
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

    private class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return true;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
