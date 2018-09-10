package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.model.Lecture
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
import com.anoop.iistconnectfaculty.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_view_attendance.*
import java.text.SimpleDateFormat

class ViewAttendanceActivity : AppCompatActivity() {

    private var adapter: FirestoreRecyclerAdapter<Lecture, LectureViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attendance)

        lectureList.layoutManager = LinearLayoutManager(this)

        lectureList.itemAnimator = DefaultItemAnimator()

        setupRecyclerView()

    }

    private fun setupRecyclerView() {

        val mDatabase = FirebaseFirestore.getInstance()

        val courseId = intent.getStringExtra("courseId")

        val query = mDatabase.collection(Constants.courseCollection).document(courseId).collection(Constants.lectureCollection)

        val response = FirestoreRecyclerOptions.Builder<Lecture>()
                .setQuery(query, Lecture::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<Lecture, LectureViewHolder>(response) {

            override fun onBindViewHolder(holder: LectureViewHolder, position: Int, model: Lecture) {

                holder.bind(model)

                holder.itemView.setOnClickListener {

                    val intent = Intent(this@ViewAttendanceActivity, LectureAttendanceActivity::class.java)
                    intent.putExtra("lecture", model)
                    startActivity(intent)
                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.lecute_row, parent, false)
                return LectureViewHolder(view)
            }
        }

        adapter!!.notifyDataSetChanged()
        lectureList.adapter = adapter


    }


    class LectureViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        val date: TextView = view.findViewById(R.id.lecute_date)

        fun bind( lecture: Lecture) {

            val simpleDateFormat = SimpleDateFormat("EEEE : dd/MM/yyyy")

            val time = simpleDateFormat.format(lecture.timestamp)
            date.text = time

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


}
