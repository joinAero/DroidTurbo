package cc.cubone.turbo.model;

public class Card {

    private String mTitle;
    private String mDescription;
    private String mImagePath;

    public Card() {
    }

    public Card(String title, String description, String imagePath) {
        mTitle = title;
        mDescription = description;
        mImagePath = imagePath;
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

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

}
