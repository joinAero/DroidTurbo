package cc.cubone.turbo.model;

import android.graphics.drawable.Drawable;

public class DataInfo<Data> extends Info {

    private Data mData;

    public DataInfo(String title, String description, Drawable drawable, Data data) {
        super(title, description, drawable);
        mData = data;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

}
