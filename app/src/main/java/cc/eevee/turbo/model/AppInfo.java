package cc.eevee.turbo.model;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppInfo extends DataInfo<ApplicationInfo> {

    public enum Type {
        UNDEFINED, SYSTEM, USER,
    }

    public enum State {
        UNDEFINED, STOPPED, RUNNING,
    }

    private Type mType = Type.UNDEFINED;
    private State mState = State.UNDEFINED;

    public AppInfo(String title, String description, Drawable drawable, ApplicationInfo info) {
        super(title, description, drawable, info);
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

}
