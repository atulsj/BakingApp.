package youtubeapidemo.examples.com.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import youtubeapidemo.examples.com.bakingapp.sync.JobDispatcher;

/*Main Activity contains fragment MainActivityFragment*/

public class MainActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener {


    public static ProgressBar mProgressBar;
    private int previosPosition;
    public static final String LIST_KEY = "clicked item position";
    public static final String BUNDLE = "bundle";
    public static final String INGREDIENT_ACTIVITY_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar= (ProgressBar) findViewById(R.id.prog_main);
        if(savedInstanceState==null)
            mProgressBar.setVisibility(View.VISIBLE);
       /* if (savedInstanceState != null && !savedInstanceState.isEmpty() && savedInstanceState.containsKey("CLICKED_POSITION")) {
            previosPosition = savedInstanceState.getInt("CLICKED_POSITION", 0);
        }*/
/*
        mIntentFilter=new IntentFilter();
        mNetworkChangeBroadcastReciever=new NetworkChangeBroadcastReciever();
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);*/

        JobDispatcher.scheduleChargingReminder(this);
    }

    @Override
    public void onListItemClick(int clickedItemIndex,String name) {

        if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
            previosPosition = clickedItemIndex;
            Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString(INGREDIENT_ACTIVITY_NAME,name);
            bundle.putInt(LIST_KEY,clickedItemIndex);
            intent.putExtra(BUNDLE, bundle);
            intent.setAction("DETAILS");
            startActivity(intent);
        } /*else
        if (previosPosition != clickedItemIndex) {
            progressBar.setVisibility(View.VISIBLE);
            DetailIngredientFragment detailIngredientFragment = new DetailIngredientFragment();
            previosPosition = clickedItemIndex;
            detailIngredientFragment.setPosition(clickedItemIndex);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_ingredient_fragment,
                    detailIngredientFragment).commit();
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("CLICKED_POSITION", previosPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //    registerReceiver(mNetworkChangeBroadcastReciever,mIntentFilter);

    }
/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkChangeBroadcastReciever);
    }*/
/*
    private class NetworkChangeBroadcastReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
          //      MainActivityFragment.makeJsonArrayRequest(context);
            }
        }
    }*/
}
