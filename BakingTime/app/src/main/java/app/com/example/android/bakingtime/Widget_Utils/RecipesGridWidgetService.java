package app.com.example.android.bakingtime.Widget_Utils;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class RecipesGridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("WIDGETCLICK", "RecipesGridWidgetService onGetViewFactory");
        return new RecipesGridRemoteViewsFactory(this.getApplicationContext());
    }
}
