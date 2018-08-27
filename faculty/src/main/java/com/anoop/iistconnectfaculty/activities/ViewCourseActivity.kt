package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.dialog.QRDialog
import `in`.rgpvnotes.alert.myresource.model.Course
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anoop.iistconnectfaculty.R
import kotlinx.android.synthetic.main.activity_view_course.*

class ViewCourseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_course)

        val course = intent.getSerializableExtra("model") as Course
        supportActionBar!!.title = course.courseName

        courseBrief.text = course.courseBrief

        classLocation.text = course.location

        manageButton.setOnClickListener {


        }


        addButton.setOnClickListener {

            val qrDialog = QRDialog(this@ViewCourseActivity, course.courseId!!, "Scan this QR code to enroll in " + course.courseName)
            qrDialog.show()

        }

    }
}
