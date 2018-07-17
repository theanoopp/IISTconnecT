package in.rgpvnotes.alert.myresource.models;

import java.io.Serializable;

/**
 * Created by anoop on 16/2/18.
 */

public class StudentModel implements Serializable{

    private String studentName;//
    private String studentEmail;//
    private String studentBranch;//
    private String profileImage;//
    private String enrollmentNumber;//
    private String batch;//
    private String verified;
    private String section;//
    private String currentYear;//
    private String classId;
    private String currentSemester;//
    private String program;

    public StudentModel() {

    }

    public StudentModel(String studentName, String studentEmail, String studentBranch, String profileImage, String enrollmentNumber, String batch, String verified, String section, String currentYear, String classId, String currentSemester,String program) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentBranch = studentBranch;
        this.profileImage = profileImage;
        this.enrollmentNumber = enrollmentNumber;
        this.batch = batch;
        this.verified = verified;
        this.section = section;
        this.currentYear = currentYear;
        this.classId = classId;
        this.currentSemester = currentSemester;
        this.program = program;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentBranch() {
        return studentBranch;
    }

    public void setStudentBranch(String studentBranch) {
        this.studentBranch = studentBranch;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(String currentSemester) {
        this.currentSemester = currentSemester;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}