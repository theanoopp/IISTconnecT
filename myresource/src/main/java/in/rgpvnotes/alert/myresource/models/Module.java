package in.rgpvnotes.alert.myresource.models;

import java.io.Serializable;
import java.util.Date;

public class Module implements Serializable {

    private String courseName;
    private String courseBrief;
    private String location;
    private Date startDate;
    private Date endDate;
    private String facultyName;
    private String facultyEmail;


    public Module() {
    }

    public Module(String courseName, String courseBrief, String location, Date startDate, Date endDate, String facultyName, String facultyEmail) {
        this.courseName = courseName;
        this.courseBrief = courseBrief;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.facultyName = facultyName;
        this.facultyEmail = facultyEmail;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseBrief() {
        return courseBrief;
    }

    public void setCourseBrief(String courseBrief) {
        this.courseBrief = courseBrief;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyEmail() {
        return facultyEmail;
    }

    public void setFacultyEmail(String facultyEmail) {
        this.facultyEmail = facultyEmail;
    }
}
