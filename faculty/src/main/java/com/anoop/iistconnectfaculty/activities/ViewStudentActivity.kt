package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.model.StudentModel
import `in`.rgpvnotes.alert.myresource.utils.Constants
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anoop.iistconnectfaculty.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_view_student.*

class ViewStudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_student)

        val studentId = intent.getStringExtra("studentId")

        val database = FirebaseFirestore.getInstance()

        database.collection(Constants.STUDENTS_COLLECTION).document(studentId).get().addOnSuccessListener {

            val student : StudentModel = it.toObject(StudentModel::class.java)!!
            studentName.text = student.studentName
            studentEnroll.text = student.enrollmentNumber

        }.addOnFailureListener {

            studentName.text = "Unable to get Student info.."
            Toast.makeText(applicationContext,it.localizedMessage, Toast.LENGTH_SHORT).show()

        }


    }
}
