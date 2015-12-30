package cc.cubone.turbo.model;

public class DataCard<Data> extends Card {

    private Data mData;

    public DataCard(String title, String description, String imagePath, Data data) {
        super(title, description, imagePath);
        mData = data;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

}
