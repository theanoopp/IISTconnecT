package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.model.StudentModel
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anoop.iistconnectfaculty.R
import kotlinx.android.synthetic.main.activity_view_student.*

class ViewStudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_student)

        val student = intent.getSerializableExtra("student") as StudentModel

        studentName.text = student.studentName
        studentEnroll.text = student.enrollmentNumber


    }
}
