package app.com.example.android.bakingtime.Widget_Utils;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class IngredientsGridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("WIDGETCLICK", "IngredientGridWidgetService onGetViewFactory");

        return new IngredientsGridRemoteViewFactory(this.getApplicationContext());
    }
}
