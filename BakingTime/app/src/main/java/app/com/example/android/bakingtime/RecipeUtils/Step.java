package app.com.example.android.bakingtime.RecipeUtils;

public class Step {
    private String id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;

    public String getId() {
        return id;
    }

    public void setId(String mId) {
        this.id = mId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String mShortDescription) {
        this.shortDescription = mShortDescription;
    }

    public String getFullDescription() {
        return description;
    }

    public void setFullDescription(String mFullDescription) {
        this.description = mFullDescription;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String mVideoURL) {
        this.videoURL = mVideoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String mThumbnailURL) {
        this.thumbnailURL = mThumbnailURL;
    }
}
