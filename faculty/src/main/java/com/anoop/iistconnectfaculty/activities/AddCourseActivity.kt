package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.dialog.MyProgressDialog
import `in`.rgpvnotes.alert.myresource.model.Course
import `in`.rgpvnotes.alert.myresource.utils.Constants
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.anoop.iistconnectfaculty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_course.*

class AddCourseActivity : AppCompatActivity() {

    private val dialog by lazy { MyProgressDialog(this@AddCourseActivity) }

    private lateinit var courseName: String

    private lateinit var courseBrief: String

    private lateinit var classLocation: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)


        submitCourse.setOnClickListener {

            if (checkInput()) {

                dialog.setTitle("Please wait...")
                dialog.setMessage("Creating...")
                dialog.setCancelable(false)

                dialog.show()

                submitCourse.isEnabled = false

                val auth = FirebaseAuth.getInstance()
                val firebaseUser = auth.currentUser

                val database = FirebaseFirestore.getInstance()

                val courseId = database.collection(Constants.courseCollection).document().id

                val course = Course(courseName, courseBrief, classLocation, firebaseUser!!.displayName, firebaseUser.uid, firebaseUser.email, courseId)

                database.collection(Constants.courseCollection).document(courseId).set(course).addOnSuccessListener {
                    Toast.makeText(this@AddCourseActivity, "New Course added", Toast.LENGTH_SHORT).show()
                    finish()
                    dialog.dismiss()
                }.addOnFailureListener { e ->
                    dialog.dismiss()
                    Toast.makeText(this@AddCourseActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }


            }


        }


    }


    private fun checkInput(): Boolean {

        var result = true

        courseNameInput.error = null

        briefInput.error = null

        location.error = null

        courseName = courseNameInput.editText!!.text.toString()

        courseBrief = briefInput.editText!!.text.toString()

        classLocation = location.editText!!.text.toString()

        if (courseName.length < 4) {

            courseNameInput.error = "Enter course name"

            result = false

        }
        if (classLocation.length < 4) {

            location.error = "Enter classroom location"
            result = false

        }

        return result
    }


}
