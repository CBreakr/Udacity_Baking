package app.com.example.android.bakingtime.UI_Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import app.com.example.android.bakingtime.R;

public class FragmentMovement {

    public enum TransitionAnimation{
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    private int mTargetId;
    private TransitionAnimation mTransitionType;
    private Fragment mFragment;
    private String mBackTag;

    public FragmentMovement(int target, TransitionAnimation anim, Fragment frag, String backTag){
        mTargetId = target;
        mTransitionType = anim;
        mFragment = frag;
        mBackTag = backTag;
    }

    /*
    from Hiren Patel:
    https://stackoverflow.com/questions/4932462/animate-the-transition-between-fragments
     */
    public void replaceFragmentWithAnimation(FragmentActivity fa)
    {
        FragmentTransaction transaction = fa.getSupportFragmentManager().beginTransaction();
        setAnimation(transaction);
        transaction.replace(mTargetId, mFragment);
        transaction.addToBackStack(mBackTag);
        transaction.commit();
    }

    private void setAnimation(FragmentTransaction transaction){
        switch(mTransitionType){
            case LEFT:
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case RIGHT:
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case TOP:
                transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);
                break;
            case BOTTOM:
                transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom);
                break;
        }
    }
}
