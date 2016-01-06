package cc.cubone.turbo.model;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppCard extends DataCard<ApplicationInfo> {

    public enum Type {
        Undefined,
        User,
        System
    }

    public enum State {
        Undefined,
        Dead,
        Running
    }

    private Type mType = Type.Undefined;
    private State mState = State.Undefined;

    public AppCard(String title, String description, Drawable drawable, ApplicationInfo info) {
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
