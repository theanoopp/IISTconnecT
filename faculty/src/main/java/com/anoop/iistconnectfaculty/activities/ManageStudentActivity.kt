package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.model.StudentModel
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
import com.anoop.iistconnectfaculty.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_manage_student.*

class ManageStudentActivity : AppCompatActivity() {

    private var adapter: FirestoreRecyclerAdapter<StudentModel, StudentViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_student)

        studentList.layoutManager = LinearLayoutManager(this)

        studentList.itemAnimator = DefaultItemAnimator()

        setupRecyclerView()

    }

    private fun setupRecyclerView() {

        val mDatabase = FirebaseFirestore.getInstance()

        val courseId = intent.getStringExtra("courseId")

        val query = mDatabase.collection(Constants.courseCollection).document(courseId).collection(Constants.enrolledStudents)

        val response = FirestoreRecyclerOptions.Builder<StudentModel>()
                .setQuery(query, StudentModel::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<StudentModel, StudentViewHolder>(response) {

            override fun onBindViewHolder(holder: StudentViewHolder, position: Int, model: StudentModel) {

                holder.bind(model)

                holder.itemView.setOnClickListener {

                    val intent = Intent(this@ManageStudentActivity, ViewStudentActivity::class.java)
                    intent.putExtra("student", model)
                    startActivity(intent)

                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.course_row, parent, false)
                return StudentViewHolder(view)
            }
        }

        adapter!!.notifyDataSetChanged()
        studentList.adapter = adapter


    }

    public override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    public override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }



    class StudentViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        val studentName: TextView = view.findViewById(R.id.courseName)
        val studentEnroll: TextView = view.findViewById(R.id.courseBrief)

        fun bind(student: StudentModel) {

            studentName.text = student.studentName
            studentEnroll.text = student.enrollmentNumber

        }

    }

}
