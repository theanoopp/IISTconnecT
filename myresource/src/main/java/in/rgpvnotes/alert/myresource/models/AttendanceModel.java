package in.rgpvnotes.alert.myresource.models;

import java.util.Date;

public class AttendanceModel {

    private String studentName;
    private String enrollmentNumber;
    private boolean present;
    private Date date;
    private String classId;
    private String subjectCode;
    private String subjectName;
    private String semester;
    private String facultyId;
    private String facultyName;
    private String timeSlot;

    public AttendanceModel() {
    }

    public AttendanceModel(String studentName, String enrollmentNumber, boolean present, Date date, String classId, String subjectCode, String subjectName, String semester, String facultyId, String facultyName, String timeSlot) {
        this.studentName = studentName;
        this.enrollmentNumber = enrollmentNumber;
        this.present = present;
        this.date = date;
        this.classId = classId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.semester = semester;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.timeSlot = timeSlot;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}
