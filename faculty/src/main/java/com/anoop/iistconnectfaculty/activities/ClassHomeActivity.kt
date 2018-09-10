package com.anoop.iistconnectfaculty.activities

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
import com.anoop.iistconnectfaculty.R

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_class_home.*

class ClassHomeActivity : AppCompatActivity() {

    private var adapter: FirestoreRecyclerAdapter<Course, ClassViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_home)

        classList.layoutManager = LinearLayoutManager(this)

        classList.itemAnimator = DefaultItemAnimator()

        addFab.setOnClickListener{

            val intent = Intent(this, AddCourseActivity::class.java)
            startActivity(intent)

        }


        setupRecyclerView()



    }

    private fun setupRecyclerView() {

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val mDatabase = FirebaseFirestore.getInstance()

        val query = mDatabase.collection(Constants.courseCollection).whereEqualTo("facultyId",firebaseUser?.uid)

        val response = FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query, Course::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<Course, ClassViewHolder>(response) {

            override fun onBindViewHolder(holder: ClassViewHolder, position: Int, model: Course) {

                holder.bind(model)

                infoText.visibility = View.GONE

                holder.itemView.setOnClickListener {

                    val intent = Intent(this@ClassHomeActivity, ViewCourseActivity::class.java)
                    intent.putExtra("model", model)
                    startActivity(intent)

                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.course_row, parent, false)
                return ClassViewHolder(view)
            }
        }

        adapter!!.notifyDataSetChanged()
        classList.adapter = adapter


    }

    class ClassViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        val clsName: TextView = view.findViewById(R.id.courseName)
        val batch: TextView = view.findViewById(R.id.courseBrief)

        fun bind(course: Course) {

            clsName.text = course.courseName
            batch.text = course.courseBrief

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
