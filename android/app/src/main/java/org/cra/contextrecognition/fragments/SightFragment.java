package org.cra.contextrecognition.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;


public class SightFragment extends Fragment {

    private boolean mUserSeen = false;
    private boolean mViewCreated = false;

    public SightFragment() {
    }

    /*public boolean isUserSeen() {
        return mUserSeen;
    }

    public boolean isViewCreated() {
        return mViewCreated;
    }*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!mUserSeen && isVisibleToUser) {
            mUserSeen = true;
            onUserFirstSight();
            tryViewCreatedFirstSight();
        }
        onUserVisibleChanged(isVisibleToUser);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Override this if you want to get savedInstanceState.
        mViewCreated = true;
        tryViewCreatedFirstSight();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewCreated = false;
        mUserSeen = false;
    }

    private void tryViewCreatedFirstSight() {
        if (mUserSeen && mViewCreated) {
            onViewCreatedFirstSight(getView());
        }
    }

    /**
     * Called when the new created view is visible to user for the first time.
     */
    protected void onViewCreatedFirstSight(View view) {
        // handling here
    }

    /**
     * Called when the fragment's UI is visible to user for the first time.
     *
     * <p>However, the view may not be created currently if in {@link android.support.v4.view.ViewPager}.
     */
    protected void onUserFirstSight() {
    }

    /**
     * Called when the visible state to user has been changed.
     */
    protected void onUserVisibleChanged(boolean visible) {
    }

}