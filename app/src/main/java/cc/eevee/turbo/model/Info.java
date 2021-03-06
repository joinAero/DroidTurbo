package cc.eevee.turbo.model;

import android.graphics.drawable.Drawable;

public class Info {

    private String mTitle;
    private String mDescription;
    private Drawable mDrawable;

    public Info() {
    }

    public Info(String title, String description, Drawable drawable) {
        mTitle = title;
        mDescription = description;
        mDrawable = drawable;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }
}
