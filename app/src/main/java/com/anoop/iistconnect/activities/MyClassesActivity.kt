package com.anoop.iistconnect.activities

import `in`.rgpvnotes.alert.myresource.dialog.MyProgressDialog
import `in`.rgpvnotes.alert.myresource.model.Course
import `in`.rgpvnotes.alert.myresource.utils.Constants
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.anoop.iistconnect.R
import com.anoop.iistconnect.utils.SessionManagement
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_my_classes.*
import android.R.attr.data
import android.app.Activity
import android.graphics.Bitmap
import android.util.Log.d
import com.google.firebase.firestore.FieldValue
import java.util.HashMap


class MyClassesActivity : AppCompatActivity() {

    private var adapter: FirestoreRecyclerAdapter<Course, ClassViewHolder>? = null

    private val mDatabase = FirebaseFirestore.getInstance()

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_classes)

        classList.layoutManager = LinearLayoutManager(this)

        classList.itemAnimator = DefaultItemAnimator()

        //Todo: check user first if null logout the app

        addFab.setOnClickListener {

            val addScan = IntentIntegrator(this@MyClassesActivity)
            addScan.setOrientationLocked(false)
            val intent = addScan.createScanIntent()
            startActivityForResult(intent, 5)

        }

        markButton.setOnClickListener {

            val markScan = IntentIntegrator(this@MyClassesActivity)
            markScan.setOrientationLocked(false)
            val intent = markScan.createScanIntent()
            startActivityForResult(intent, 6)

        }
        setupRecyclerView()

    }

    private fun setupRecyclerView() {

        val query = mDatabase.collection(Constants.STUDENTS_COLLECTION).document(user!!.uid).collection(Constants.EnrolledCourses)

        val response = FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query, Course::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<Course, ClassViewHolder>(response) {

            override fun onBindViewHolder(holder: ClassViewHolder, position: Int, model: Course) {

                holder.bind(model)

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.course_row, parent, false)
                return ClassViewHolder(view)
            }
        }

        adapter!!.notifyDataSetChanged()
        classList.adapter = adapter


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode != RESULT_CANCELED) {

            val result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data)

            if (requestCode == 5) {

                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {

                    val courseId = result.contents
                    addToCourse(courseId)
                }
            }else if(requestCode == 6){

                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {

                    val string = result.contents

                    val separate1 = string.split("@_@".toRegex())


                    val lectureId = separate1[0]
                    val courseId = separate1[1]

                    markAttendance(courseId,lectureId)

                }


            }

        }

    }

    private fun markAttendance(courseId: String,lectureId: String){


        val dialog = MyProgressDialog(this@MyClassesActivity)
        dialog.setTitle("Loading...")
        dialog.setMessage("Please wait..")
        dialog.show()

        val student = FirebaseAuth.getInstance().currentUser

        val studentId = student!!.uid

        val getAttendanceQuery = mDatabase.collection(Constants.courseCollection).document(courseId).collection(Constants.attendanceCollection).whereEqualTo("lectureId", lectureId).whereEqualTo("studentId",studentId)
        getAttendanceQuery.get().addOnSuccessListener { it ->

            val a = it.size()
            if(a == 0){

                val checkCourseEnroll = mDatabase.collection(Constants.courseCollection).document(courseId).collection(Constants.enrolledStudents).document(studentId)
                checkCourseEnroll.get().addOnSuccessListener { documentSnapshot ->

                    if(documentSnapshot.exists()){

                        d("MY_E",documentSnapshot.id)

                        val studentModel =  SessionManagement.getStudent(this@MyClassesActivity)

                        val map = HashMap<String, Any?>()
                        map["studentName"] = studentModel.studentName
                        map["studentEnrollment"] = studentModel.enrollmentNumber
                        map["timestamp"] = FieldValue.serverTimestamp()
                        map["lectureId"] = lectureId
                        map["courseId"] = courseId
                        map["studentId"] = studentId

                        mDatabase.collection(Constants.courseCollection).document(courseId).collection(Constants.attendanceCollection).document().set(map).addOnSuccessListener {

                            dialog.dismiss()
                            Toast.makeText(this@MyClassesActivity, "Attendance Marked", Toast.LENGTH_SHORT).show()


                        }.addOnFailureListener {

                            dialog.dismiss()
                            Toast.makeText(this@MyClassesActivity, "Unable to mark attendance", Toast.LENGTH_SHORT).show()

                        }

                    }else{

                        dialog.dismiss()
                        Toast.makeText(this@MyClassesActivity, "You don't belong to this Class", Toast.LENGTH_SHORT).show()

                    }

                }.addOnFailureListener {

                    dialog.dismiss()
                    Toast.makeText(this@MyClassesActivity, "Unable to fetch information", Toast.LENGTH_SHORT).show()

                }



            }else{

                dialog.dismiss()
                Toast.makeText(this@MyClassesActivity, "Attendance is already marked", Toast.LENGTH_SHORT).show()

            }

        }.addOnFailureListener {

            dialog.dismiss()
            Toast.makeText(this@MyClassesActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()

        }




    }

    private fun addToCourse(courseId: String) {

        val dialog = MyProgressDialog(this@MyClassesActivity).apply {
            setTitle("Loading...")
            setMessage("Please wait..")
            show()
        }
        mDatabase.collection(Constants.courseCollection).document(courseId).get().addOnSuccessListener {

            if (it.exists()) {
                val course = it.toObject(Course::class.java)
                val student = SessionManagement.getStudent(this)

                if (user != null && student != null && course != null) {

                    val batch = mDatabase.batch()

                    val facRef = mDatabase.collection(Constants.courseCollection).document(courseId).collection(Constants.enrolledStudents).document(user.uid)
                    batch.set(facRef, student)

                    val studentRef = mDatabase.collection(Constants.STUDENTS_COLLECTION).document(user.uid).collection(Constants.EnrolledCourses).document(courseId)
                    batch.set(studentRef, course)

                    batch.commit().addOnSuccessListener { _ ->
                        dialog.dismiss()
                        Toast.makeText(this@MyClassesActivity, "Added", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        dialog.dismiss()
                        Toast.makeText(this@MyClassesActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    dialog.dismiss()
                    Toast.makeText(this@MyClassesActivity, "There was some error", Toast.LENGTH_SHORT).show()
                }

            }


        }


    }


    public override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    public override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }


    class ClassViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val clsName: TextView = view.findViewById(R.id.courseName)
        private val batch: TextView = view.findViewById(R.id.courseBrief)

        fun bind(course: Course) {

            clsName.text = course.courseName
            batch.text = course.courseBrief

        }

    }

}

