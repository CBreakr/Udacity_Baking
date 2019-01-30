package app.com.example.android.bakingtime.UI_Utils;

import android.os.Parcel;
import android.os.Parcelable;

public class SubState implements Parcelable {
    private int mRecipeIndex;
    private int mStepIndex;
    private StateManagement.UserActionSubType mCurrentNavigationActionSubType;
    private StateManagement.UserActionSubType mCurrentDetailActionSubType;
    private StateManagement.ScreenMode mScreen;

    public SubState(){}

    public int getRecipeIndex() {
        return mRecipeIndex;
    }

    public void setRecipeIndex(int mRecipeIndex) {
        this.mRecipeIndex = mRecipeIndex;
    }

    public int getStepIndex() {
        return mStepIndex;
    }

    public void setStepIndex(int mStepIndex) {
        this.mStepIndex = mStepIndex;
    }

    public StateManagement.UserActionSubType getCurrentNavigationActionSubType() {
        return mCurrentNavigationActionSubType;
    }

    public void setCurrentNavigationActionSubType(StateManagement.UserActionSubType mCurrentNavigationActionSubType) {
        this.mCurrentNavigationActionSubType = mCurrentNavigationActionSubType;
    }

    public StateManagement.UserActionSubType getCurrentDetailActionSubType() {
        return mCurrentDetailActionSubType;
    }

    public void setCurrentDetailActionSubType(StateManagement.UserActionSubType mCurrentDetailActionSubType) {
        this.mCurrentDetailActionSubType = mCurrentDetailActionSubType;
    }

    public StateManagement.ScreenMode getScreen() {
        return mScreen;
    }

    public void setScreen(StateManagement.ScreenMode mScreen) {
        this.mScreen = mScreen;
    }

    public SubState copy(){
        SubState sub = new SubState();

        sub.mRecipeIndex = mRecipeIndex;
        sub.mStepIndex = mStepIndex;
//        sub.mCurrentNavigationActionSubType = mCurrentNavigationActionSubType;
        if(mCurrentNavigationActionSubType != null) {
            switch (mCurrentNavigationActionSubType) {
                case INGREDIENTS:
                    sub.mCurrentNavigationActionSubType = StateManagement.UserActionSubType.INGREDIENTS;
                    break;
                case INDIVIDUALSTEP:
                    sub.mCurrentNavigationActionSubType = StateManagement.UserActionSubType.INDIVIDUALSTEP;
                    break;
                case STEPLIST:
                    sub.mCurrentNavigationActionSubType = StateManagement.UserActionSubType.STEPLIST;
                    break;
                case RECIPELIST:
                    sub.mCurrentNavigationActionSubType = StateManagement.UserActionSubType.RECIPELIST;
                    break;
                default:
                    sub.mCurrentNavigationActionSubType = null;
            }
        }
//        sub.mCurrentDetailActionSubType = mCurrentDetailActionSubType;
        if(mCurrentDetailActionSubType != null) {
            switch (mCurrentDetailActionSubType) {
                case INGREDIENTS:
                    sub.mCurrentDetailActionSubType = StateManagement.UserActionSubType.INGREDIENTS;
                    break;
                case INDIVIDUALSTEP:
                    sub.mCurrentDetailActionSubType = StateManagement.UserActionSubType.INDIVIDUALSTEP;
                    break;
                case STEPLIST:
                    sub.mCurrentDetailActionSubType = StateManagement.UserActionSubType.STEPLIST;
                    break;
                case RECIPELIST:
                    sub.mCurrentDetailActionSubType = StateManagement.UserActionSubType.RECIPELIST;
                    break;
                default:
                    sub.mCurrentDetailActionSubType = null;
            }
        }
        sub.mScreen = mScreen;

        return sub;
    }

    //
    // PARCELABLE
    //

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SubState createFromParcel(Parcel in) {
            return new SubState(in);
        }

        public SubState[] newArray(int size) {
            return new SubState[size];
        }
    };

    public SubState(Parcel in){
        this.setRecipeIndex(in.readInt());
        this.setStepIndex(in.readInt());

        String nav = in.readString();
        StateManagement.UserActionSubType navigation = nav == null ? null :
                StateManagement.UserActionSubType.forInt(Integer.parseInt(nav));
        this.setCurrentNavigationActionSubType(navigation);

        String det = in.readString();
        StateManagement.UserActionSubType detail = det == null ? null :
                StateManagement.UserActionSubType.forInt(Integer.parseInt(det));
        this.setCurrentDetailActionSubType(detail);

        this.setScreen(StateManagement.ScreenMode.forInt(in.readInt()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.getRecipeIndex());
        out.writeInt(this.getStepIndex());

        StateManagement.UserActionSubType navigation = this.getCurrentNavigationActionSubType();
        String nav = navigation == null ? null : String.valueOf(navigation.getValue());
        out.writeString(nav);

        StateManagement.UserActionSubType detail = this.getCurrentDetailActionSubType();
        String det = detail == null ? null : String.valueOf(detail.toString());
        out.writeString(det);

        out.writeInt(this.getScreen().getValue());
    }
}
