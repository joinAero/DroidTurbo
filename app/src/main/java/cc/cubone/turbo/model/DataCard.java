package cc.cubone.turbo.model;

import android.graphics.drawable.Drawable;

public class DataCard<Data> extends Card {

    private Data mData;

    public DataCard(String title, String description, Drawable drawable, Data data) {
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
