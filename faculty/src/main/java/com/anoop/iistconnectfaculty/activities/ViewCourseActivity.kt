package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.dialog.MyProgressDialog
import `in`.rgpvnotes.alert.myresource.dialog.QRDialog
import `in`.rgpvnotes.alert.myresource.model.Course
import `in`.rgpvnotes.alert.myresource.model.Lecture
import `in`.rgpvnotes.alert.myresource.utils.Constants
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anoop.iistconnectfaculty.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_view_course.*
import java.util.HashMap

class ViewCourseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_course)

        val course = intent.getSerializableExtra("model") as Course
        supportActionBar!!.title = course.courseName

        courseBrief.text = course.courseBrief

        classLocation.text = course.location

        manageButton.setOnClickListener {

            val intent = Intent(this@ViewCourseActivity, ManageStudentActivity::class.java)
            intent.putExtra("courseId", course.courseId)
            startActivity(intent)


        }


        addButton.setOnClickListener {

            val qrDialog = QRDialog(this@ViewCourseActivity, course.courseId!!, "Scan this QR code to enroll in " + course.courseName)
            qrDialog.show()

        }

        takeButton.setOnClickListener { it ->

            val dialog = MyProgressDialog(this@ViewCourseActivity)
            dialog.setTitle("Loading...")
            dialog.setMessage("Please wait..")
            dialog.show()

            val firestore = FirebaseFirestore.getInstance()
            val lectureId = firestore.collection(Constants.courseCollection).document(course.courseId!!).collection(Constants.lectureCollection).document().id

            val map = HashMap<String, Any>()
            map["lectureId"] = lectureId
            map["courseId"] = course.courseId!!
            map["timestamp"] = FieldValue.serverTimestamp()
            map["facultyId"] = course.facultyId!!

            firestore.collection(Constants.courseCollection).document(course.courseId!!).collection(Constants.lectureCollection).document(lectureId).set(map).addOnSuccessListener {

                dialog.dismiss()

                val string = lectureId+ "@_@" +course.courseId

                val qrDialog = QRDialog(this@ViewCourseActivity,string, "Scan this QR code to mark Attendance in " + course.courseName)
                qrDialog.show()


            }.addOnFailureListener {

                dialog.dismiss()
                Toast.makeText(this@ViewCourseActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()

            }



        }

        viewButton.setOnClickListener {

            val intent = Intent(this@ViewCourseActivity, ViewAttendanceActivity::class.java)
            intent.putExtra("courseId", course.courseId)
            startActivity(intent)

        }

    }
}
