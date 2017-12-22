package youtubeapidemo.examples.com.bakingapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DescriptionActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            finish();
        }
    }
}
