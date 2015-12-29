package cc.cubone.turbo.ui.base;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    private boolean mUserNotSeen = true;

    public BaseFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mUserNotSeen && isVisibleToUser) {
            onUserFirstSight();
            mUserNotSeen = false;
        }
        onUserVisibleChanged(isVisibleToUser);
    }

    /**
     * Called when visible to user first time.
     */
    protected void onUserFirstSight() {
    }

    /**
     * Called when visible to user has changed.
     *
     * <ul>
     * <li><a href="http://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager">
     * How to determine when Fragment becomes visible in ViewPager</a>
     * </ul>
     */
    protected void onUserVisibleChanged(boolean visible) {
    }

}
