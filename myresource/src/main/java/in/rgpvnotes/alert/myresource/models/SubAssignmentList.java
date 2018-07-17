package in.rgpvnotes.alert.myresource.models;

/**
 * Created by Anoop on 06-03-2018.
 */

public class SubAssignmentList {

    private String subjectName;
    private String subjectCode;
    private String section;

    public SubAssignmentList() {
    }

    public SubAssignmentList(String subjectName, String subjectCode, String section) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.section = section;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public boolean equals(Object obj) {

        boolean retVal = false;

        if (obj instanceof SubAssignmentList){
            SubAssignmentList ptr = (SubAssignmentList) obj;
            retVal = ptr.subjectCode.equals(this.subjectCode);
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.subjectCode != null ? this.subjectCode.hashCode() : 0);
        return hash;
    }





}
