package com.anoop.iistconnect.activities

import `in`.rgpvnotes.alert.myresource.dialog.MyProgressDialog
import `in`.rgpvnotes.alert.myresource.model.Course
import `in`.rgpvnotes.alert.myresource.model.StudentModel
import `in`.rgpvnotes.alert.myresource.utils.Constants
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.anoop.iistconnect.R
import com.anoop.iistconnect.utils.SessionManagement
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_my_classes.*

class MyClassesActivity : AppCompatActivity() {

    private var adapter: FirestoreRecyclerAdapter<Course, ClassViewHolder>? = null

    private val mDatabase = FirebaseFirestore.getInstance()

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_classes)

        classList.layoutManager = LinearLayoutManager(this)

        classList.itemAnimator = DefaultItemAnimator()

        addFab.setOnClickListener{

            val addScan = IntentIntegrator(this@MyClassesActivity)
            addScan.setOrientationLocked(false)
            val intent = addScan.createScanIntent()
            startActivityForResult(intent, 5)

        }


        //setupRecyclerView()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == 5) {

            val result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data)

            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {

                    val courseId = result.contents
                    addToCourse(courseId)
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun addToCourse(courseId: String) {

        val dialog = MyProgressDialog(this@MyClassesActivity).apply {
            setTitle("Loading...")
            setMessage("Please wait..")
            show()
        }

        val student = SessionManagement.getStudent(this)

        if (user != null) {
            mDatabase.collection(Constants.courseCollection).document(courseId).collection(Constants.enrolledStudents).document(user.uid).set(student).addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(this@MyClassesActivity, "Added", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this@MyClassesActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }


    class ClassViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        val clsName: TextView = view.findViewById(R.id.courseName)
        val batch: TextView = view.findViewById(R.id.courseBrief)

        fun bind(course: Course) {

            clsName.text = course.courseName
            batch.text = course.courseBrief

        }

    }

}
