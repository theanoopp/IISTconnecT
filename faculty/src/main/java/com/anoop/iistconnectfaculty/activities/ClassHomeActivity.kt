package com.anoop.iistconnectfaculty.activities

import `in`.rgpvnotes.alert.myresource.model.Course
import `in`.rgpvnotes.alert.myresource.utils.Constants
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.anoop.iistconnectfaculty.R

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_class_home.*
import java.util.HashMap

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

        private val clsName: TextView = view.findViewById(R.id.courseName)
        private val courseBrief: TextView = view.findViewById(R.id.courseBrief)
        private val loadingBar : ProgressBar = view.findViewById(R.id.progressBar)

        fun bind(course: Course) {

            loadingBar.visibility = View.GONE

            clsName.text = course.courseName
            courseBrief.text = course.courseBrief

            clsName.visibility = View.VISIBLE
            courseBrief.visibility = View.VISIBLE

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
