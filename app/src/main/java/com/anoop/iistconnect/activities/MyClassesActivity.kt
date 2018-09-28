package com.anoop.iistconnect.activities

import `in`.rgpvnotes.alert.myresource.dialog.MyProgressDialog
import `in`.rgpvnotes.alert.myresource.model.Course
import `in`.rgpvnotes.alert.myresource.model.StudentCoursesMap
import `in`.rgpvnotes.alert.myresource.utils.Constants
import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_my_classes.*
import android.util.Log.d
import android.widget.ProgressBar
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.functions.FirebaseFunctions
import java.util.HashMap


class MyClassesActivity : AppCompatActivity() {

    private var adapter: FirestoreRecyclerAdapter<StudentCoursesMap, ClassViewHolder>? = null

    private val mDatabase = FirebaseFirestore.getInstance()

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_classes)

        classList.layoutManager = LinearLayoutManager(this)

        classList.itemAnimator = DefaultItemAnimator()

        //Todo: check user first if null logout the app

        enrollButton.setOnClickListener {

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

        val query = mDatabase.collection(Constants.studentCoursesMap).whereEqualTo("studentId", user!!.uid)

        val response = FirestoreRecyclerOptions.Builder<StudentCoursesMap>()
                .setQuery(query, StudentCoursesMap::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<StudentCoursesMap, ClassViewHolder>(response) {

            override fun onBindViewHolder(holder: ClassViewHolder, position: Int, model: StudentCoursesMap) {

                tip.visibility = View.GONE

                holder.bind(model,applicationContext)

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

                    val dialog = MyProgressDialog(this@MyClassesActivity).apply {
                        setTitle("Loading...")
                        setMessage("Please wait..")
                        show()
                    }

                    addToCourse(courseId).addOnSuccessListener {

                        dialog.dismiss()
                        Toast.makeText(this@MyClassesActivity,it, Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener {

                        dialog.dismiss()
                        Toast.makeText(this@MyClassesActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }else if(requestCode == 6){

                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {

                    val string = result.contents

                    val separate1 = string.split("@_@".toRegex())


                    val lectureId = separate1[0]
                    val courseId = separate1[1]

                    val dialog = MyProgressDialog(this@MyClassesActivity)
                    dialog.setTitle("Loading...")
                    dialog.setMessage("Please wait..")
                    dialog.show()

                    markAttendance(courseId,lectureId).addOnSuccessListener {
                        dialog.dismiss()
                        Toast.makeText(this@MyClassesActivity, it, Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(this@MyClassesActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                }


            }

        }

    }

    private fun markAttendance(courseId: String,lectureId: String): Task<String> {

        val mFunctions = FirebaseFunctions.getInstance()
        val student = FirebaseAuth.getInstance().currentUser
        val studentId = student!!.uid

        val data = HashMap<String, Any>()

        data["courseId"] = courseId
        data["studentId"] = studentId
        data["lectureId"] = lectureId

        return mFunctions
                .getHttpsCallable("markAttendance")
                .call(data)
                .continueWith { task ->

                    task.result.data as String

                }


    }

    private fun addToCourse(courseId: String): Task<String> {

        val mFunctions = FirebaseFunctions.getInstance()

        val data = HashMap<String, Any>()

        data["courseId"] = courseId
        data["studentId"] = user!!.uid

        return mFunctions
                .getHttpsCallable("addToCourse")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    task.result.data as String

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
        private val courseBrief: TextView = view.findViewById(R.id.courseBrief)
        private val loadingBar : ProgressBar = view.findViewById(R.id.progressBar)

        fun bind( map: StudentCoursesMap ,context: Context) {

            // TODO : it can be realtime :P
            val database = FirebaseFirestore.getInstance()

            database.collection(Constants.courseCollection).document(map.courseId!!).get().addOnSuccessListener {

                if(it.exists()){
                    loadingBar.visibility = View.GONE

                    val course : Course = it.toObject(Course::class.java)!!

                    clsName.text = course.courseName
                    courseBrief.text = course.courseBrief

                    clsName.visibility = View.VISIBLE
                    courseBrief.visibility = View.VISIBLE
                }else{
                    loadingBar.visibility = View.GONE
                    clsName.visibility = View.VISIBLE
                    clsName.text = "Unable to get course info.."
                }



            }.addOnFailureListener {

                loadingBar.visibility = View.GONE
                clsName.visibility = View.VISIBLE
                clsName.text = "Unable to get Course info.."
                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }

        }

    }

}

