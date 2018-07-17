package in.rgpvnotes.alert.myresource.models;

/**
 * Created by Anoop on 18-03-2018.
 */

public class PhotosModel {

    private String title;
    private String description;
    private String downloadURL;

    public PhotosModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }
}
