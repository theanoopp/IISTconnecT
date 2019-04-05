package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.model.Attendance
import `in`.rgpvnotes.alert.myresource.model.Lecture
import `in`.rgpvnotes.alert.myresource.model.Student
import `in`.rgpvnotes.alert.myresource.utils.Constants
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.anoop.iistconnectfaculty.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_lecture_attendance.*



class LectureAttendanceActivity : AppCompatActivity() {

    private var adapter: FirestoreRecyclerAdapter<Student, ViewHolder>? = null

    private val mDatabase = FirebaseFirestore.getInstance()

    private var lecture : Lecture? = null

    private val list = arrayListOf<Attendance>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecture_attendance)

        lecture = intent.getSerializableExtra("lecture") as Lecture

        studentList.layoutManager = LinearLayoutManager(this)

        studentList.itemAnimator = DefaultItemAnimator()

        val query = mDatabase.collection(Constants.courseCollection).document(lecture!!.courseId!!)
                .collection(Constants.attendanceCollection).whereEqualTo("lectureId", lecture!!.lectureId)

        query.get().addOnSuccessListener {


            for (document in it.documents) {
                val attendance = document.toObject(Attendance::class.java)
                if (attendance != null) {
                    list.add(attendance)
                }
            }

            setupRecyclerView()

        }.addOnFailureListener {



        }



    }

    private fun setupRecyclerView() {

        val query = mDatabase.collection(Constants.courseCollection).document(lecture!!.courseId!!).collection(Constants.enrolledStudents)

        val response = FirestoreRecyclerOptions.Builder<Student>()
                .setQuery(query, Student::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<Student, ViewHolder>(response) {

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Student) {

                val attendance = Attendance()

                attendance.studentId = model.enrollmentNumber

                val boolean = list.contains(attendance)
                holder.bind(model,boolean)

                holder.itemView.setOnClickListener {

                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.lecute_row, parent, false)
                return ViewHolder(view)
            }
        }

        adapter!!.notifyDataSetChanged()
        studentList.adapter = adapter

        adapter!!.startListening()


    }

    public override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }


    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val date: TextView = view.findViewById(R.id.lecuteDay)

        fun bind(student: Student, boolean: Boolean) {

            if(boolean){
                date.text = student.studentName + " was present"
            }else{
                date.text = student.studentName
            }


        }
    }


}
