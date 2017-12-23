package youtubeapidemo.examples.com.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import static youtubeapidemo.examples.com.bakingapp.provider.BakeContact.BASE_CONTENT_URI;
import static youtubeapidemo.examples.com.bakingapp.provider.BakeContact.BakeEntry;
import static youtubeapidemo.examples.com.bakingapp.provider.BakeContact.PATH_RECIPES;


public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all plant info ordered by creation time
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0)
            return null;
        mCursor.moveToPosition(position);
        int name_Index = mCursor.getColumnIndex(BakeEntry.COLUMN_NAME);
        String recipeName = mCursor.getString(name_Index);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.baking_widget_provider);
        views.setTextViewText(R.id.appwidget_text,recipeName );
        Bundle extras = new Bundle();
        extras.putInt("WIDGIT_POSITION_CLICKED", position);
        Intent fillInIntent = new Intent();
        fillInIntent.setAction(IngredientActivityFragment.WIDGIT_POSITION);
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.appwidget_text, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}