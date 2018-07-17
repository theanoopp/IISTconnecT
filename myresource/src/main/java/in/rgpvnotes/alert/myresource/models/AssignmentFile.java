package in.rgpvnotes.alert.myresource.models;

import android.net.Uri;

/**
 * Created by Anoop on 04-03-2018.
 */

public class AssignmentFile {

    private Uri uri;
    private String name;

    public AssignmentFile() {
    }

    public AssignmentFile(Uri uri, String name) {
        this.uri = uri;
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
