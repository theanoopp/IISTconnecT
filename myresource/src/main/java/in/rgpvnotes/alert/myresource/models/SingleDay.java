package in.rgpvnotes.alert.myresource.models;

/**
 * Created by anoop on 23/2/18.
 */

public class SingleDay {

    private SingleLecture lecture1;
    private SingleLecture lecture2;
    private SingleLecture lecture3;
    private SingleLecture lecture4;
    private SingleLecture lecture5;
    private SingleLecture lecture6;
    private SingleLecture lecture7;
    private SingleLecture lecture8;

    public SingleDay() {
    }

    public SingleDay(SingleLecture lecture1, SingleLecture lecture2, SingleLecture lecture3, SingleLecture lecture4, SingleLecture lecture5, SingleLecture lecture6, SingleLecture lecture7, SingleLecture lecture8) {
        this.lecture1 = lecture1;
        this.lecture2 = lecture2;
        this.lecture3 = lecture3;
        this.lecture4 = lecture4;
        this.lecture5 = lecture5;
        this.lecture6 = lecture6;
        this.lecture7 = lecture7;
        this.lecture8 = lecture8;
    }


    public SingleLecture getLecture1() {
        return lecture1;
    }

    public void setLecture1(SingleLecture lecture1) {
        this.lecture1 = lecture1;
    }

    public SingleLecture getLecture2() {
        return lecture2;
    }

    public void setLecture2(SingleLecture lecture2) {
        this.lecture2 = lecture2;
    }

    public SingleLecture getLecture3() {
        return lecture3;
    }

    public void setLecture3(SingleLecture lecture3) {
        this.lecture3 = lecture3;
    }

    public SingleLecture getLecture4() {
        return lecture4;
    }

    public void setLecture4(SingleLecture lecture4) {
        this.lecture4 = lecture4;
    }

    public SingleLecture getLecture5() {
        return lecture5;
    }

    public void setLecture5(SingleLecture lecture5) {
        this.lecture5 = lecture5;
    }

    public SingleLecture getLecture6() {
        return lecture6;
    }

    public void setLecture6(SingleLecture lecture6) {
        this.lecture6 = lecture6;
    }

    public SingleLecture getLecture7() {
        return lecture7;
    }

    public void setLecture7(SingleLecture lecture7) {
        this.lecture7 = lecture7;
    }

    public SingleLecture getLecture8() {
        return lecture8;
    }

    public void setLecture8(SingleLecture lecture8) {
        this.lecture8 = lecture8;
    }
}
