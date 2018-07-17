package in.rgpvnotes.alert.myresource.models;

/**
 * Created by Anoop on 23-02-2018.
 */

public class SingleLecture {

    private String subjectCode;
    private String subjectName;
    private String time;


    public SingleLecture() {
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
