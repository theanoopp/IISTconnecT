package in.rgpvnotes.alert.myresource.models;

/**
 * Created by anoop on 23/2/18.
 */

public class SingleClassSchedule {

    private SingleDay monday;
    private SingleDay tuesday;
    private SingleDay wednesday;
    private SingleDay thursday;
    private SingleDay friday;
    private SingleDay saturday;

    public SingleClassSchedule() {
    }

    public SingleClassSchedule(SingleDay monday, SingleDay tuesday, SingleDay wednesday, SingleDay thursday, SingleDay friday, SingleDay saturday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
    }

    public SingleDay getMonday() {
        return monday;
    }

    public void setMonday(SingleDay monday) {
        this.monday = monday;
    }

    public SingleDay getTuesday() {
        return tuesday;
    }

    public void setTuesday(SingleDay tuesday) {
        this.tuesday = tuesday;
    }

    public SingleDay getWednesday() {
        return wednesday;
    }

    public void setWednesday(SingleDay wednesday) {
        this.wednesday = wednesday;
    }

    public SingleDay getThursday() {
        return thursday;
    }

    public void setThursday(SingleDay thursday) {
        this.thursday = thursday;
    }

    public SingleDay getFriday() {
        return friday;
    }

    public void setFriday(SingleDay friday) {
        this.friday = friday;
    }

    public SingleDay getSaturday() {
        return saturday;
    }

    public void setSaturday(SingleDay saturday) {
        this.saturday = saturday;
    }
}
