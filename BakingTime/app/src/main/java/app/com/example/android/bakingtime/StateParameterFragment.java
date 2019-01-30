package app.com.example.android.bakingtime;

import android.support.v4.app.Fragment;
import app.com.example.android.bakingtime.UI_Utils.StateManagement;

public abstract class StateParameterFragment extends Fragment {
    public abstract void fillParameters(StateManagement state);
    public abstract String getBackStackTag();
}
