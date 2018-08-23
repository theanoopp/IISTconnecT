package com.anoop.iistconnectfaculty.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.anoop.iistconnectfaculty.R;

import in.rgpvnotes.alert.myresource.model.AttendanceModel;
import in.rgpvnotes.alert.myresource.model.StudentModel;

public class AttendanceViewHolder extends RecyclerView.ViewHolder  {

    public TextView studentName;
    private TextView enroll;
    public CheckBox checkBox;
    private View mView;

    public AttendanceViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        studentName = itemView.findViewById(R.id.attendance_student_name);
        enroll = itemView.findViewById(R.id.attendance_student_enrollment);
        checkBox = itemView.findViewById(R.id.attendance_checkBox);
    }

    public void bind(AttendanceModel model){

        studentName.setText(model.getStudentName());
        enroll.setText(model.getEnrollmentNumber());

    }

    public void setCheckBox(boolean value) {
        checkBox.setChecked(value);
    }

}
