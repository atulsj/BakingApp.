package youtubeapidemo.examples.com.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class IngredientActivity extends AppCompatActivity {

    public static ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);
        mProgressBar = (ProgressBar) findViewById(R.id.prog_ingredient);
        ActionBar actionBar = getSupportActionBar();
        Intent intent=getIntent();
        String activityName = "Baking Activity";
        if(intent!=null){
            if(intent.hasExtra(MainActivity.BUNDLE)){
                Bundle bundle=intent.getBundleExtra(MainActivity.BUNDLE);
                activityName=bundle.getString(MainActivity.INGREDIENT_ACTIVITY_NAME);
            }
        }
        if (actionBar != null) {
            actionBar.setTitle(activityName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
